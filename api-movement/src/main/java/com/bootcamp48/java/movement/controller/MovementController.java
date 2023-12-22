package com.bootcamp48.java.movement.controller;

import com.bootcamp48.java.movement.api.MovementApi;
import com.bootcamp48.java.movement.model.Movement;
import com.bootcamp48.java.movement.service.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/api/microservice/1.0.0")
@RestController
@Slf4j
public class MovementController implements MovementApi {

    private final MovementService service;

    @Override
    public Mono<ResponseEntity<Flux<Movement>>> getMovements(ServerWebExchange exchange) {
        return Mono.just(service.findAll())
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<Movement>>> findAccountById(String accountId, ServerWebExchange exchange) {
        return Mono.just(service.findByAccountId(accountId))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}