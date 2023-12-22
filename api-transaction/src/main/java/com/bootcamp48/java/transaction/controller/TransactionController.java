package com.bootcamp48.java.transaction.controller;

import com.bootcamp48.java.transaction.api.TransactionApi;
import com.bootcamp48.java.transaction.model.Transaction;
import com.bootcamp48.java.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/api/microservice/1.0.0")
@RestController
@Slf4j
public class TransactionController implements TransactionApi {

    private final TransactionService service;

    @Override
    public Mono<ResponseEntity<Transaction>> addTransaction(Mono<Transaction> customer, ServerWebExchange exchange) {
        return service.save(customer)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

}