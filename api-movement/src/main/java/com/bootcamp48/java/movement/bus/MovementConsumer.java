package com.bootcamp48.java.movement.bus;

import com.bootcamp48.java.movement.model.Movement;
import com.bootcamp48.java.movement.service.MovementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;


@Slf4j
@Service
public class MovementConsumer {
    @Autowired
    private MovementService service;

    @KafkaListener(topics = "transaction_topic", groupId = "transaction-movement-consumer")
    public void consumeMessage(String message) {
        log.info("Consuming Movement: {}.", message);
        var parts = message.split("\\|");

        try {
            Movement movementModel = new Movement();
            movementModel.setIdTransaction(parts[3]);
            movementModel.setTransactionType(parts[1]);

            // Manejo de errores al intentar parsear la cadena a BigDecimal
            try {
                movementModel.setAmount(new BigDecimal(parts[2]));
            } catch (NumberFormatException e) {
                log.error("Error parsing amount: {}", e.getMessage());
                return; // Termina la ejecución del método en caso de error
            }

            movementModel.setAccountId(parts[0]);

            this.service.save(Mono.just(movementModel))
                    .onErrorResume(error -> {
                        log.error("Error saving movement: {}", error.getMessage());
                        return Mono.empty(); // Retorna un Mono vacío para continuar con el flujo
                    })
                    .subscribe();
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }

}