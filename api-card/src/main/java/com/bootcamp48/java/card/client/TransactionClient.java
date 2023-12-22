package com.bootcamp48.java.card.client;

import com.bootcamp48.java.card.model.Transaction;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.webjars.NotFoundException;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@Component
@ReactiveFeignClient(name = "transaction-microservice", url = "${transaction.service.url}", path = "${transaction.service.path}")
public interface TransactionClient {

    @PostMapping(value = "/account/{accountId}/transaction")
    @CircuitBreaker(name = "TransactionCircuitBreaker", fallbackMethod = "TransactionSaveFallback")
    Mono<Transaction> save(@PathVariable("accountId") String accountId, @RequestBody Mono<Transaction> transaction);

    @Slf4j
    final class LogHolder
    {}

    default Mono<Transaction> TransactionSaveFallback(String accountId, Mono<Transaction> transaction, Throwable e) {
        LogHolder.log.error("failure Transaction-Microservice: " + e.getCause().getMessage());
        throw new NotFoundException(e.getCause().getMessage());
    }
}
