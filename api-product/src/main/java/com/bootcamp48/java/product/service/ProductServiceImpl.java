package com.bootcamp48.java.product.service;

import com.bootcamp48.java.product.exception.InvalidDataException;
import com.bootcamp48.java.product.mapper.ProductMapper;
import com.bootcamp48.java.product.model.Product;
import com.bootcamp48.java.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    private final ReactiveRedisTemplate<String, Product> reactiveRedisTemplate;

    public Mono<Product> save(Mono<Product> product) {
        return product.map(this::validation)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel);
    }

    private Product validation(Product c) {
        if (c.getType() == Product.TypeEnum.PASIVOS){
            c.setBankCredit(null);
            if (c.getBankAccount() == null)
                throw new InvalidDataException("Account must not be null");
        }
        else if (c.getType() == Product.TypeEnum.ACTIVOS) {
            c.setBankAccount(null);
            if (c.getBankCredit() == null)
                throw new InvalidDataException("Credit must not be null");
        }
        return c;
    }

    public Mono<Void> deleteById(String id) {
        return findById(id)
                .map(mapper::toDocument)
                .flatMap(repository::delete);
    }

    public Mono<Product> findById(String id) {
        return reactiveRedisTemplate.opsForValue().get(id)
                .switchIfEmpty(repository.findById(id)
                        .map(mapper::toModelWithoutDate)
                        .doOnSubscribe(s -> log.info("Getting Product with ID {}", id))
                        .flatMap(p -> reactiveRedisTemplate.opsForValue().set(id, p).thenReturn(p))
                );
    }

    public Flux<Product> findAll() {
        return repository.findAll()
                .map(mapper::toModel);
    }

    public Mono<Product> update(Mono<Product> product, String id) {
        return save(findById(id)
                .flatMap(c -> product.doOnNext(x -> x.setCreatedAt(c.getCreatedAt())))
                .doOnNext(e -> e.setId(id)));
    }

    @Override
    public Flux<Product> findAllByAccount_Name(Collection<String> account_names) {
        return repository.findAllByBankAccount_NameIn(account_names.stream().map(String::toUpperCase).collect(Collectors.toList()))
                .map(mapper::toModel);
    }
}