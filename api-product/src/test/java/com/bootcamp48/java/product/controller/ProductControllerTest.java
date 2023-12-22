package com.bootcamp48.java.product.controller;

import com.bootcamp48.java.product.model.Product;
import com.bootcamp48.java.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(ProductController.class)
class ProductControllerTest {

    private static final String ENDPOINT_URL = "/api/microservice/1.0.0/product";
    @MockBean
    private ProductService service;
    @Autowired
    WebTestClient webTestClient;

    @Test
    void delete() {
        Mockito.when(service.deleteById(ArgumentMatchers.anyString())).thenReturn(Mono.empty());

        webTestClient.delete().uri(ENDPOINT_URL + "/636dd894a3da3a2e59d90ad2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
        Mockito.verify(service, Mockito.times(1)).deleteById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void findAll() {
        Mockito.when(service.findAll()).thenReturn(Flux.just(ProductBuilder.getDto()));

        webTestClient.get().uri(ENDPOINT_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
        Mockito.verify(service, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void findById() {
        Mockito.when(service.findById(ArgumentMatchers.anyString())).thenReturn(Mono.just(ProductBuilder.getDto()));

        webTestClient.get().uri(ENDPOINT_URL + "/636dd894a3da3a2e59d90ad2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
        Mockito.verify(service, Mockito.times(1)).findById("636dd894a3da3a2e59d90ad2");
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void save() {
        Mockito.when(service.save(ArgumentMatchers.any())).thenReturn(Mono.just(ProductBuilder.getDto()));

        webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(ProductBuilder.getDto()), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
        Mockito.verify(service, Mockito.times(1)).save(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void update() {
        Mockito.when(service.update(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(Mono.just(ProductBuilder.getDto()));

        webTestClient.put().uri(ENDPOINT_URL + "/636dd894a3da3a2e59d90ad2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(ProductBuilder.getDto()), Product.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
        Mockito.verify(service, Mockito.times(1)).update(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(service);
    }
}