package com.bootcamp48.java.card.repository;

import com.bootcamp48.java.card.domain.Card;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CardRepository extends ReactiveMongoRepository<Card, String> {
    Flux<Card> findAllByTitularId(String customerId);
    Mono<Card> findByNumberCardOrCvv(String numberCard, String cvv);
}
