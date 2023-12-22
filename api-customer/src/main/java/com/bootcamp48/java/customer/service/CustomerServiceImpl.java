package com.bootcamp48.java.customer.service;

import com.bootcamp48.java.customer.exception.InvalidDataException;
import com.bootcamp48.java.customer.mapper.CustomerMapper;
import com.bootcamp48.java.customer.model.Customer;
import com.bootcamp48.java.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository repository;

    private final CustomerMapper mapper;

    @Override
    public Mono<Customer> save(Mono<Customer> customer) {
        return customer.map(this::validation)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toModel);
    }

    private Customer validation(Customer c) {
        if (c.getType() == Customer.TypeEnum.PERSONAL){
            c.setBusiness(null);
            if (c.getPersonal() == null)
                throw new InvalidDataException("Personal must not be null");
        }
        else if (c.getType() == Customer.TypeEnum.BUSINESS){
            if (c.getBusiness() == null)
                throw new InvalidDataException("Business must not be null");
        }
        return c;
    }

    @Override
    public Mono<Customer> findById(String id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Customer> findByDocument(String document) {
        return repository.findFirstByPersonal_DocumentNumberOrBusiness_Ruc(Mono.just(document), Mono.just(document))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Customer> update(Mono<Customer> customer, String id) {
        return save(findById(id)
                .flatMap(c -> customer.doOnNext(x -> x.setCreatedAt(c.getCreatedAt())))
                .doOnNext(e -> e.setId(id)));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return findById(id)
                .map(mapper::toDocument)
                .flatMap(repository::delete);
    }

    @Override
    public Mono<Customer> findByPhone(String phone) {
        return repository.findFirstByPhone(phone)
                .map(mapper::toModel);
    }
}