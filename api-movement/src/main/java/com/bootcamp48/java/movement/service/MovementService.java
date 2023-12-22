package com.bootcamp48.java.movement.service;

import com.bootcamp48.java.movement.model.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementService {
    Mono<Movement> save(Mono<Movement> movement);
    Flux<Movement> findAll();
    Flux<Movement> findByAccountId(String accountId);

}
