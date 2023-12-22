package com.bootcamp48.java.wallet.service;

import com.bootcamp48.java.swap.event.Event;
import com.bootcamp48.java.swap.event.EventType;
import com.bootcamp48.java.wallet.bus.WalletProducer;
import com.bootcamp48.java.wallet.exception.InvalidDataException;
import com.bootcamp48.java.wallet.mapper.WalletMapper;
import com.bootcamp48.java.wallet.model.Coin;
import com.bootcamp48.java.wallet.model.Swap;
import com.bootcamp48.java.wallet.model.Wallet;
import com.bootcamp48.java.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final WalletMapper mapper;
    private final WalletProducer producer;

    @Override
    public Mono<Wallet> save(Mono<Wallet> wallet) {
        return wallet.filter(this::validation)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel);
    }

    @Override
    public Flux<Wallet> findAll() {
        return repository.findAll()
                .map(mapper::toModel);
    }

    @Override
    public Mono<Wallet> findById(String id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Wallet> update(Mono<Wallet> wallet, String id) {
        return save(findById(id)
                .flatMap(c -> wallet.doOnNext(x -> x.setCreatedAt(c.getCreatedAt())))
                .doOnNext(e -> e.setId(id)));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return findById(id)
                .map(mapper::toDocument)
                .flatMap(repository::delete);
    }

    @Override
    public Mono<Wallet> findByCustomer(String customer) {
        return repository.findFirstByCustomerId(customer)
                .map(mapper::toModel);
    }

    private Boolean validation(Wallet wallet) {
        long distinctCoinType = wallet.getCoin().stream().map(Coin::getCoinType).distinct().count();
        if (distinctCoinType != wallet.getCoin().size()) {
            throw new InvalidDataException("Coin type can not be repeated");
        }
        return true;
    }

    @Override
    public Mono<Void> validBySwap(Swap swap) {
        AtomicBoolean isTransactionValid = new AtomicBoolean(true);
        AtomicReference<EventType> eventType = new AtomicReference<>(EventType.VALID);
        return findById(swap.getWalletId())
                .switchIfEmpty(Mono.defer(() -> {
                    isTransactionValid.set(false);
                    eventType.set(EventType.NOT_FOUND);
                    return Mono.empty();
                }))
                .flatMap(wallet -> {
                    Coin coin = findCoinByType(wallet, swap.getCoinType());
                    if (coin == null) {
                        isTransactionValid.set(false);
                        eventType.set(EventType.INVALID_COIN);
                        return Mono.empty();
                    } else if (coin.getAvailableAmount().add(swap.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                        isTransactionValid.set(false);
                        eventType.set(EventType.LOW_BALANCE);
                        return Mono.empty();
                    } else {
                        coin.setAvailableAmount(coin.getAvailableAmount().add(swap.getAmount()));
                        sendEventMessage(swap.getId(), eventType.get());
                        return update(Mono.just(wallet), swap.getWalletId());
                    }
                })
                .flatMap(wallet -> findById(swap.getReferenceId())
                        .switchIfEmpty(Mono.defer(() -> {
                            isTransactionValid.set(false);
                            eventType.set(EventType.INVALID_DESTINATION);
                            return Mono.empty();
                        }))
                )
                .thenEmpty(t -> {
                    if (!isTransactionValid.get()) {
                        sendEventMessage(swap.getId(), eventType.get());
                    }
                });
    }

    private Coin findCoinByType(Wallet wallet, String coinType) {
        return wallet.getCoin().stream()
                .filter(c -> c.getCoinType().getValue().equals(coinType))
                .findFirst()
                .orElse(null);
    }

    private void sendEventMessage(String id, EventType eventType) {
        Event<Wallet> event = new Event<>();
        event.setId(id);
        event.setType(eventType);
        producer.publish(event);
    }

}
