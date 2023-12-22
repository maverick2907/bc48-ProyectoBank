package com.bootcamp48.java.account.service;

import com.bootcamp48.java.account.bus.Producer;
import com.bootcamp48.java.account.client.CustomerClient;
import com.bootcamp48.java.account.client.ProductClient;
import com.bootcamp48.java.account.exception.InvalidDataException;
import com.bootcamp48.java.account.mapper.AccountMapper;
import com.bootcamp48.java.account.model.Account;
import com.bootcamp48.java.account.model.Customer;
import com.bootcamp48.java.account.model.Product;
import com.bootcamp48.java.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    public static final String CUSTOMER_PERSONAL = "personal";
    public static final String CUSTOMER_BUSINESS = "business";
    public static final String SAVINGS_ACCOUNT = "Ahorro";
    public static final String CURRENT_ACCOUNT = "Corriente";
    public static final String FIXED_TERM_ACCOUNT = "Plazo fijo";


    private final AccountRepository repository;

    private final AccountMapper mapper;

    private final CustomerClient customerClient;

    private final ProductClient productClient;

    private final Producer producer;

    public Mono<Account> save(Mono<Account> account) {
        return account.filterWhen(this::validTitular)
                .filterWhen(this::validSignatory)
                .map(this::validBalance)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel)
                .doOnNext(a -> this.producer.sendMessage(a.getId() + "|" + a.getTitularId().toString()));
    }

    public Mono<Void> deleteById(String id) {
        return findById(id)
                .map(mapper::toDocument)
                .flatMap(repository::delete);
    }

    public Mono<Account> findById(String id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    public Flux<Account> findAll() {
        return repository.findAll()
                .map(mapper::toModel);
    }

    public Mono<Account> update(Mono<Account> account, String id) {
        return save(findById(id)
                .flatMap(c -> account.doOnNext(x -> x.setCreatedAt(c.getCreatedAt())))
                .doOnNext(e -> e.setId(id)));
    }

    @Override
    public Flux<Account> findAllByTitularId(List<String> titularId) {
        return repository.findAllByTitularId(titularId)
                .map(mapper::toModel);
    }

    private Mono<Customer> getCustomer(String id) {
        return customerClient.findById(id);
    }

    private Mono<Product> getProduct(String id) {
        return productClient.findById(id);
    }

    private Flux<Product> getProducts() {
        return productClient.findAllByName(Arrays.asList(SAVINGS_ACCOUNT, CURRENT_ACCOUNT, FIXED_TERM_ACCOUNT));
    }

    private Account validBalance(Account account) {
        if (account.getAvailableBalance() == null || account.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidDataException("Las cuentas bancarias tienen un monto mínimo de apertura que puede ser cero");
        return account;
    }

    private Flux<Boolean> validSignatory(Account account) {
        if (account.getSignatoryId() == null || account.getSignatoryId().isEmpty()) {
            return Flux.just(true);
        }
        return Flux.fromIterable(account.getSignatoryId())
                .flatMap(id -> getCustomer(id).flatMap(c -> {
                    if (!Objects.equals(c.getType(), CUSTOMER_BUSINESS))
                        return Mono.error(new InvalidDataException("El cliente " + id + " no es valido"));
                    return Mono.just(true);
                }));
    }

    private Flux<Boolean> validTitular(Account account) {
        AtomicBoolean isPersonal = new AtomicBoolean(false);
        if (account.getTitularId() == null || account.getTitularId().isEmpty()) {
            throw new InvalidDataException("Se debe ingresar un titular");
        }
        return Flux.fromIterable(account.getTitularId())
                .flatMap(id -> getCustomer(id).flatMap(c -> {
                    if (isPersonal.get()) {
                        return Mono.error(new InvalidDataException("Solo las cuentas bancarias empresariales pueden tener uno o más titulares"));
                    } else if (Objects.equals(c.getType(), CUSTOMER_PERSONAL)) {
                        isPersonal.set(true);
                        return validPersonal(account, findAllByTitularId(Collections.singletonList(id))
                                .filter(x -> !x.getId().equals(account.getId())), getProducts());
                    } else if (Objects.equals(c.getType(), CUSTOMER_BUSINESS)) {
                        return validBusiness(account);
                    }
                    return Mono.just(false);
                }).doOnNext(b -> {
                    if (!b)
                        throw new InvalidDataException("El cliente " + id + " no es valido");
                }));
    }

    private Mono<Boolean> validBusiness(Account account) {
        return getProduct(account.getProductId())
                .map(p -> Objects.equals(p.getBankAccount().getName(), CURRENT_ACCOUNT))
                .map(v -> {
                    if (!v)
                        throw new InvalidDataException("Un cliente empresarial no puede tener una cuenta de ahorro o de plazo fijo");
                    return true;
                });
    }

    private Mono<Boolean> validPersonal(Account account, Flux<Account> accounts, Flux<Product> products) {
        return accountExists(products, accounts, SAVINGS_ACCOUNT)
                .map(b -> {
                    if (b)
                        throw new InvalidDataException("Un cliente personal solo puede tener un máximo de una cuenta de ahorro");
                    return true;
                })
                .zipWith(accountExists(products, accounts, CURRENT_ACCOUNT))
                .map(b -> {
                    if (b.getT2())
                        throw new InvalidDataException("Un cliente personal solo puede tener un máximo de una cuenta corriente");
                    return true;
                })
                .zipWith(accountExists(products, accounts, FIXED_TERM_ACCOUNT))
                .flatMap(b -> getProduct(account.getProductId())
                        .map(p -> Objects.equals(p.getBankAccount().getName(), FIXED_TERM_ACCOUNT))
                        .map(v -> {
                            if (!v && b.getT2())
                                throw new InvalidDataException("Un cliente personal solo puede tener un máximo cuentas a plazo fijo");
                            return true;
                        }));
    }

    private Mono<Boolean> accountExists(Flux<Product> products, Flux<Account> accounts, String savingsAccount) {
        Mono<String> productId = products.filter(p -> Objects.equals(p.getBankAccount().getName(), savingsAccount))
                .map(Product::getId)
                .next();
        return accounts.zipWith(productId)
                .filter(a -> Objects.equals(a.getT1().getProductId(), a.getT2()))
                .count()
                .map(i -> i > 0);
    }
}