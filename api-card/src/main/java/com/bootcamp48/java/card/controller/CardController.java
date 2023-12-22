package com.bootcamp48.java.card.controller;

import com.bootcamp48.java.card.api.CardApi;
import com.bootcamp48.java.card.model.Card;
import com.bootcamp48.java.card.model.Consume;
import com.bootcamp48.java.card.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class CardController implements CardApi {

    private final CardService service;

    @Override
    public Mono<ResponseEntity<Card>> addCard(Mono<Card> card, ServerWebExchange exchange) {
        return service.save(card)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCardById(String id, ServerWebExchange exchange) {
        return service.deleteById(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Card>> findCardById(String id, ServerWebExchange exchange) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<Card>>> getCards(ServerWebExchange exchange) {
        return Mono.just(service.findAll())
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Card>> updateCard(String id, Mono<Card> card, ServerWebExchange exchange) {
        return service.update(card, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<Card>>> findCardByCustomerId(String customerId, ServerWebExchange exchange) {
        return Mono.just(service.findAllByCustomer(customerId))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Card>> findCardByNumber(String number, ServerWebExchange exchange) {
        return service.findByNumber(number)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Consume>> addConsume(String cardId, Mono<Consume> consume, ServerWebExchange exchange) {
        return service.saveConsume(cardId, consume)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }
}
