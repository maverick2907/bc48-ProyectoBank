package com.bootcamp48.java.movement.service;

import com.bootcamp48.java.movement.mapper.MovementMapper;
import com.bootcamp48.java.movement.model.Movement;
import com.bootcamp48.java.movement.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MovementServiceImpl implements MovementService {

    private final MovementRepository repository;

    private final MovementMapper mapper;

    @Override
    public Mono<Movement> save(Mono<Movement> movement) {
        return movement
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel);
    }

    @Override
    public Flux<Movement> findAll() {
        return repository
                .findAll()
                .map(mapper::toModel);
    }

    @Override
    public Flux<Movement> findByAccountId(String accountId) {
        return repository
                .findByAccountId(accountId)
                .map(mapper::toModel);
    }

}