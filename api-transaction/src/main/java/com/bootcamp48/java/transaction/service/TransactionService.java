package com.bootcamp48.java.transaction.service;

import com.bootcamp48.java.transaction.model.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transaction> save(Mono<Transaction> customer);

}
