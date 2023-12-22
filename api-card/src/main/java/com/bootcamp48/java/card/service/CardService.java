package com.bootcamp48.java.card.service;

import com.bootcamp48.java.card.model.Card;
import com.bootcamp48.java.card.model.Consume;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CardService {
    Mono<Card> save(Mono<Card> card);
    Mono<Void> deleteById(String id);
    Mono<Card> findById(String id);
    Flux<Card> findAll();
    Mono<Card> update(Mono<Card> card, String id);
    Flux<Card> findAllByCustomer(String customerId);
    Mono<Card> findByNumber(String number);
    Mono<Consume> saveConsume(String cardId, Mono<Consume> consume);
    Flux<Boolean> validDebt(String message);
}
