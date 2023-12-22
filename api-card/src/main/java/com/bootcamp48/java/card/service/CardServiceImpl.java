package com.bootcamp48.java.card.service;

import com.bootcamp48.java.card.client.*;
import com.bootcamp48.java.card.exception.InvalidDataException;
import com.bootcamp48.java.card.mapper.CardMapper;
import com.bootcamp48.java.card.model.*;
import com.bootcamp48.java.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CardServiceImpl implements CardService {
    public static final String CUSTOMER_PERSONAL = "personal";
    public static final String PRODUCT_ACTIVE = "Activos";
    public static final String CREDIT_PERSONAL = "Personal";
    public static final String CREDIT_BUSINESS = "Empresarial";
    public static final String TRANSFER = "Transfer";

    private final CardRepository repository;

    private final CardMapper mapper;

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final AccountClient accountClient;

    private final TransactionClient transactionClient;

    private final ConsumptionClient consumptionClient;

    @Override
    public Mono<Card> save(Mono<Card> card) {
        return card.map(this::validation)
                .filterWhen(this::validTitular)
                .filterWhen(c -> validDebt(c.getTitularId()))
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return findById(id)
                .map(mapper::toDocument)
                .flatMap(repository::delete);
    }

    @Override
    public Mono<Card> findById(String id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Flux<Card> findAll() {
        return repository.findAll()
                .map(mapper::toModel);
    }

    @Override
    public Mono<Card> update(Mono<Card> card, String id) {
        return save(findById(id)
                .flatMap(c -> card.doOnNext(x -> x.setCreatedAt(c.getCreatedAt())))
                .doOnNext(e -> e.setId(id)));
    }

    @Override
    public Flux<Card> findAllByCustomer(String customerId) {
        return repository.findAllByTitularId(customerId)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Card> findByNumber(String number) {
        return repository.findByNumberCardOrCvv(number,number)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Consume> saveConsume(String cardId, Mono<Consume> consume) {
        return consume
                .flatMap(c -> validConsume(c, cardId));
    }

    private Mono<Consume> validConsume(Consume consume, String cardId) {
        return findById(cardId)
                .flatMap(c -> {
                    consume.setCardId(c.getId());
                    if (c.getType().equals(Card.TypeEnum.CREDITO)) {
                        if (consume.getAmount().compareTo(c.getCredit().getCreditLineAvailableAmount()) > 0)
                            return Mono.error(new InvalidDataException("Se excede su límite de crédito disponible"));
                        if (consume.getBillingDay() == null || consume.getBillingDay() < 0)
                            return Mono.error(new InvalidDataException("Billing Day must not be null"));
                        if (consume.getBilledInterestsAmount() == null || consume.getBilledInterestsAmount().compareTo(BigDecimal.ZERO) < 0)
                            return Mono.error(new InvalidDataException("Billed Interests Amount must not be null"));
                        if (consume.getFixedInstallmentsQuantity() == null || consume.getFixedInstallmentsQuantity() < 0)
                            return Mono.error(new InvalidDataException("Fixed Installments Quantity must not be null"));
                        return validCard(consume, c);
                    } else if (c.getType().equals(Card.TypeEnum.DEBITO)) {
                        if (consume.getDestinationAccountId() == null || consume.getDestinationAccountId().isEmpty())
                            return Mono.error(new InvalidDataException("Destination Account ID must not be null"));
                        return validTransaction(consume, c);
                    } else return Mono.error(new InvalidDataException("Tipo de Tarjeta no valida"));
                });
    }

    private Card validation(Card c) {
        if (c.getType() == Card.TypeEnum.DEBITO) {
            c.setCredit(null);
            c.setProductId(null);
            if (c.getPrimaryAccountId() == null)
                throw new InvalidDataException("Primary Account must not be null");
        }
        else if (c.getType() == Card.TypeEnum.CREDITO) {
            if (c.getCredit() == null)
                throw new InvalidDataException("Credit must not be null");
            if (c.getProductId() == null)
                throw new InvalidDataException("Product ID must not be null");
        }
        return c;
    }

    @Override
    public Flux<Boolean> validDebt(String customerId) {
        return findAllByCustomer(customerId)
                .flatMap(c -> {
                    if (c.getCredit() != null && c.getCredit().getStatus().equals(Credit.StatusEnum.ACTIVO)) {
                        return consumptionClient.getConsumptions(c.getId())
                                .filter(b -> b.getPaymentDueDate().isBefore(LocalDate.now())).count()
                                .map(k -> {
                                    if (k > 0)
                                        throw new InvalidDataException("No podrá adquirir un producto, posee alguna deuda vencida en algún producto de crédito");
                                    else return true;
                                });
                    }
                    else return Mono.just(true);
                })
                .defaultIfEmpty(true);
    }

    private Mono<Boolean> validTitular(Card card) {
        if (card.getTitularId() == null || card.getTitularId().isEmpty()) {
            throw new InvalidDataException("Se debe ingresar un titular");
        }
        return customerClient.findById(card.getTitularId())
                .flatMap(c -> {
                    if (card.getType().equals(Card.TypeEnum.CREDITO)) {
                        return findAllByCustomer(card.getTitularId())
                                .filter(t -> t.getCredit() != null && t.getCredit().getStatus().equals(Credit.StatusEnum.ACTIVO)
                                        && !card.getId().equals(t.getId()))
                                .count()
                                .flatMap(z -> validProduct(card, c.getType(), z));
                    }
                    else {
                        return accountClient.findById(card.getPrimaryAccountId())
                                .flatMap(a -> {
                                    if (a.getTitularId().stream().noneMatch(card.getTitularId()::equals))
                                        return Mono.error(new InvalidDataException("Primary Account does not belong to the Titular"));
                                    return validAssociate(card).next();
                                });
                    }
                });
    }

    private Flux<Boolean> validAssociate(Card card) {
        if (card.getAssociatedAccountId() == null || card.getAssociatedAccountId().isEmpty()) {
            return Flux.just(true);
        }
        return Flux.fromIterable(card.getAssociatedAccountId())
                .flatMap(id -> accountClient.findById(id).flatMap(c -> {
                    if (c.getTitularId().stream().noneMatch(card.getTitularId()::equals))
                        return Mono.error(new InvalidDataException("Associated Accounts does not belong to the Titular"));
                    return Mono.just(true);
                }));
    }

    private Mono<Boolean> validProduct(Card card, String type, Long z) {
        return productClient.findById(card.getProductId())
                .flatMap(p -> {
                    if (PRODUCT_ACTIVE.equals(p.getType())) {
                        if (type.equals(CUSTOMER_PERSONAL)) {
                            if (!p.getBankCredit().getCreditCard().equals(CREDIT_PERSONAL))
                                return Mono.error(new InvalidDataException("Cliente tipo Personal solo puede tener Credito tipo Personal"));
                            if (z > (p.getBankCredit().getNumberOfCredits() - 1)) {
                                return Mono.error(new InvalidDataException("Personal: solo se permite un solo crédito por persona"));
                            }
                        }
                        else {
                            if (!p.getBankCredit().getCreditCard().equals(CREDIT_BUSINESS))
                                return Mono.error(new InvalidDataException("Cliente tipo Empresarial solo puede tener Credito tipo Empresarial"));
                        }
                        return Mono.just(true);
                    } else return Mono.error(new InvalidDataException("Tipo de Producto no valido"));
                });
    }

    private Mono<Consume> validTransaction(Consume consume, Card c) {
        var t = Transaction.builder()
                .type(TRANSFER)
                .accountId(c.getPrimaryAccountId())
                .amountPayable(consume.getAmount())
                .destinationAccountId(consume.getDestinationAccountId())
                .description(consume.getDescription())
                .build();
        return transactionClient.save(c.getPrimaryAccountId(), Mono.just(t))
                .onErrorResume(x -> validAssociated(consume, c).next())
                .map(l -> {
                    consume.setId(l.getId());
                    return consume;
                });
    }

    private Flux<Transaction> validAssociated(Consume consume, Card c) {
        AtomicBoolean completed = new AtomicBoolean(false);
        return Flux.fromIterable(c.getAssociatedAccountId())
                .flatMap(id -> {
                    var t = Transaction.builder()
                            .type(TRANSFER)
                            .accountId(id)
                            .amountPayable(consume.getAmount())
                            .destinationAccountId(consume.getDestinationAccountId())
                            .description(consume.getDescription())
                            .build();
                    if (completed.get())
                        return Mono.just(t);
                    else {
                        return transactionClient.save(c.getPrimaryAccountId(), Mono.just(t))
                                .doOnNext(n -> completed.set(true));
                    }
                });
    }

    private Mono<Consume> validCard(Consume consume, Card c) {
        var card = mapper.copy(c);
        var fixedPayAmount = consume.getBilledInterestsAmount()
                .divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(consume.getFixedInstallmentsQuantity()))
                .add(BigDecimal.ONE)
                .multiply(consume.getAmount())
                .divide(BigDecimal.valueOf(consume.getFixedInstallmentsQuantity()), 8, RoundingMode.HALF_UP);
        var consumption = Consumption.builder()
                .cardId(c.getId())
                .billingDay(consume.getBillingDay())
                .paymentDueDate(LocalDate.now().plusMonths(1).withDayOfYear(consume.getBillingDay()))
                .amount(consume.getAmount())
                .billedInterestsAmount(consume.getBilledInterestsAmount())
                .fixedInstallmentsQuantity(consume.getFixedInstallmentsQuantity())
                .currentFixedQuota(0)
                .fixedPayAmount(fixedPayAmount)
                .description(consume.getDescription())
                .createdAt(Instant.now())
                .build();
        card.getCredit().setCreditLineAvailableAmount(c.getCredit().getCreditLineAvailableAmount().subtract(consume.getAmount()));
        card.getCredit().setUsedCreditLineAmount(c.getCredit().getUsedCreditLineAmount().add(consume.getAmount()));
        return consumptionClient.save(c.getId(), Mono.just(consumption))
                .flatMap(f -> update(Mono.just(card), c.getId())
                        .flatMap(l -> {
                            consume.setId(l.getId());
                            return validTransaction(consume, c)
                                    .onErrorMap(x -> {
                                        update(Mono.just(c), c.getId())
                                                .flatMap(u -> consumptionClient.delete(c.getId(), f.getId())).subscribe();
                                        throw new InvalidDataException(x.getMessage());
                                    })
                                    .map(t -> consume);
                        }));
    }
}
