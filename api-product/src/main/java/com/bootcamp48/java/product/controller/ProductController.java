package com.bootcamp48.java.product.controller;

import com.bootcamp48.java.product.api.ProductApi;
import com.bootcamp48.java.product.model.Product;
import com.bootcamp48.java.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/microservice/1.0.0")
@RestController
@Slf4j
public class ProductController implements ProductApi {

    private final ProductService service;

    @Override
    public Mono<ResponseEntity<Void>> delete(String id, ServerWebExchange exchange) {
        return service.deleteById(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<Product>>> findAll(ServerWebExchange exchange) {
        return Mono.just(service.findAll())
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<Product>>> findAllByName(List<String> name, ServerWebExchange exchange) {
        return Mono.just(service.findAllByAccount_Name(name))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Product>> findById(String id, ServerWebExchange exchange) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Product>> save(Mono<Product> product, ServerWebExchange exchange) {
        return service.save(product)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

    @Override
    public Mono<ResponseEntity<Product>> update(String id, Mono<Product> product, ServerWebExchange exchange) {
        return service.update(product, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}