package com.bootcamp48.java.card.client;

import com.bootcamp48.java.card.exception.NotFoundException;
import com.bootcamp48.java.card.model.Consumption;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@ReactiveFeignClient(name = "consumption-microservice", url = "${consumption.service.url}", path = "${consumption.service.path}")
public interface ConsumptionClient {

    @PostMapping(value = "/card/{cardId}/consumption")
    @CircuitBreaker(name = "ConsumptionCircuitBreaker", fallbackMethod = "ConsumptionSaveFallback")
    Mono<Consumption> save(@PathVariable("cardId") String cardId, @RequestBody Mono<Consumption> consumption);

    @GetMapping(value = "/card/{cardId}/consumption")
    @CircuitBreaker(name = "ConsumptionCircuitBreaker", fallbackMethod = "ConsumptionAllFallback")
    Flux<Consumption> getConsumptions(@PathVariable("cardId") String cardId);

    @DeleteMapping(value = "/card/{cardId}/consumption/{id}")
    @CircuitBreaker(name = "ConsumptionCircuitBreaker", fallbackMethod = "ConsumptionDeleteFallback")
    Mono<Void> delete(@PathVariable("cardId") String cardId, @PathVariable("id") String id);

    @Slf4j
    final class LogHolder
    {}

    default Mono<Consumption> ConsumptionSaveFallback(String cardId, Mono<Consumption> consumption, Throwable e) {
        LogHolder.log.error("failure Consumption-Microservice: " + e.getCause().getMessage());
        throw new NotFoundException(e.getCause().getMessage());
    }

    default Flux<Consumption> ConsumptionAllFallback(String cardId, Throwable e) {
        LogHolder.log.error("failure Consumption-Microservice: " + e.getMessage());
        throw new NotFoundException("failure Consumption-Microservice: verify Card ID or try later");
    }

    default Mono<Void> ConsumptionDeleteFallback(String cardId, String id, Throwable e) {
        LogHolder.log.error("failure Consumption-Microservice: " + e.getMessage());
        throw new NotFoundException("failure Consumption-Microservice: try later");
    }
}
