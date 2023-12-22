package com.bootcamp48.java.customer.controller;

import com.bootcamp48.java.customer.api.CustomerApi;
import com.bootcamp48.java.customer.model.Customer;
import com.bootcamp48.java.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CustomerController implements CustomerApi {

    private final CustomerService service;

    @Override
    public Mono<ResponseEntity<Void>> delete(String id, ServerWebExchange exchange) {
        return service.deleteById(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Customer>> findByDocument(String document, ServerWebExchange exchange) {
        return service.findByDocument(document)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Customer>> findById(String id, ServerWebExchange exchange) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Customer>> save(Mono<Customer> customer, ServerWebExchange exchange) {
        return service.save(customer)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

    @Override
    public Mono<ResponseEntity<Customer>> update(String id, Mono<Customer> customer, ServerWebExchange exchange) {
        return service.update(customer, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Customer>> findByPhone(String phone, ServerWebExchange exchange) {
        return service.findByPhone(phone)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}