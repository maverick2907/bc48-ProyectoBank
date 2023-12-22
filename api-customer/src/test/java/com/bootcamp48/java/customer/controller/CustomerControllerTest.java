package com.bootcamp48.java.customer.controller;

import com.bootcamp48.java.customer.model.Customer;
import com.bootcamp48.java.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(CustomerController.class)
public class CustomerControllerTest {
    private static final String ENDPOINT_URL = "/api/microservice/1.0.0/customer";
    @MockBean
    private CustomerService customerService;
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void getById() {
        Mockito.when(customerService.findById(ArgumentMatchers.anyString())).thenReturn(Mono.just(CustomerBuilder.getDto()));

        webTestClient.get().uri(ENDPOINT_URL + "/636dd894a3da3a2e59d90ad2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
        Mockito.verify(customerService, Mockito.times(1)).findById("636dd894a3da3a2e59d90ad2");
        Mockito.verifyNoMoreInteractions(customerService);
    }

    @Test
    public void save() {
        Mockito.when(customerService.save(ArgumentMatchers.any())).thenReturn(Mono.just(CustomerBuilder.getDto()));

        webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(CustomerBuilder.getDto()), Customer.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
        Mockito.verify(customerService, Mockito.times(1)).save(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(customerService);
    }

    @Test
    public void update() {
        Mockito.when(customerService.update(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(Mono.just(CustomerBuilder.getDto()));

        webTestClient.put().uri(ENDPOINT_URL + "/636dd894a3da3a2e59d90ad2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(CustomerBuilder.getDto()), Customer.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
        Mockito.verify(customerService, Mockito.times(1)).update(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(customerService);
    }

    @Test
    public void delete() {
        Mockito.when(customerService.deleteById(ArgumentMatchers.anyString())).thenReturn(Mono.empty());

        webTestClient.delete().uri(ENDPOINT_URL + "/636dd894a3da3a2e59d90ad2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
        Mockito.verify(customerService, Mockito.times(1)).deleteById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(customerService);
    }
}