package com.bootcamp48.java.account.service;

import com.bootcamp48.java.account.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountService {
    Mono<Account> save(Mono<Account> account);
    Mono<Void> deleteById(String id);
    Mono<Account> findById(String id);
    Flux<Account> findAll();
    Mono<Account> update(Mono<Account> account, String id);
    Flux<Account> findAllByTitularId(List<String> customerId);
}
