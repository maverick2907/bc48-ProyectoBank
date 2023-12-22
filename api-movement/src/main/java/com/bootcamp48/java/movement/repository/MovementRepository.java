package com.bootcamp48.java.movement.repository;

import com.bootcamp48.java.movement.domain.Movement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MovementRepository extends ReactiveMongoRepository<Movement, String> {
    Flux<Movement> findByAccountId(String accountId);
}
