package com.bootcamp48.java.customer.seed;

import com.bootcamp48.java.customer.domain.Business;
import com.bootcamp48.java.customer.domain.Customer;
import com.bootcamp48.java.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CustomerSeed {
    @Autowired
    private CustomerRepository repository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        var business = Business.builder()
                .legalName("Mi Banco SAC")
                .fullName("Mi Banco")
                .ruc("20471254786")
                .profile("NORMAL")
                .build();
        var customer = Customer.builder()
                .id("63859d33434879511128cf355")
                .type("BUSINESS")
                .business(business)
                .email("soporte@mibanco.com")
                .phone("+51949747851")
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(customer.getId())
                    .switchIfEmpty(repository.save(customer)).subscribe();
        } catch (Exception ignored) { }
    }
}
