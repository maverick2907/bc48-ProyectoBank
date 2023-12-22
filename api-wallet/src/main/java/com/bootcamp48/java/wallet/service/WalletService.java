package com.bootcamp48.java.wallet.service;

import com.bootcamp48.java.wallet.model.Swap;
import com.bootcamp48.java.wallet.model.Wallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {
    Mono<Wallet> save(Mono<Wallet> wallet);
    Flux<Wallet> findAll();
    Mono<Wallet> findById(String id);
    Mono<Wallet> update(Mono<Wallet> wallet, String id);
    Mono<Void> deleteById(String id);
    Mono<Void> validBySwap(Swap swap);
    Mono<Wallet> findByCustomer(String customer);
}
