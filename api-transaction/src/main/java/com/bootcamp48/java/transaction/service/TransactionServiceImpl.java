package com.bootcamp48.java.transaction.service;

import com.bootcamp48.java.transaction.mapper.TransactionMapper;
import com.bootcamp48.java.transaction.bus.Producer;
import com.bootcamp48.java.transaction.model.Transaction;
import com.bootcamp48.java.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;

    private final TransactionMapper mapper;

    private final Producer producer;

    @Override
    public Mono<Transaction> save(Mono<Transaction> transaction) {
        return transaction
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel)
                .doOnNext(t -> this.producer.sendMessage(t.getAccountId() + "|" +
                        t.getTransactionType() + "|" + t.getAmount() + "|" + t.getId()));
    }

}