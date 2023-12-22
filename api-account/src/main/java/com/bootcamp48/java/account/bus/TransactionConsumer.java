package com.bootcamp48.java.account.bus;

import com.bootcamp48.java.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@Slf4j
@Service
public class TransactionConsumer {
    @Autowired
    private AccountService service;

    @KafkaListener(topics = "transaction_topic",groupId = "transaction-account-consumer")
    public void consumeMessage(String message) {
        log.info("Consuming Transaction: {}.", message);
        var parts = message.split("\\|");

        this.service.findById(parts[0])
                .flatMap(existingAccount -> {
                    try {
                        if ("DEPOSIT".equals(parts[1])) {
                            existingAccount.setAvailableBalance(new BigDecimal(parts[2]).add(existingAccount.getAvailableBalance()));
                        } else if ("WITHDRAWAL".equals(parts[1])) {
                            existingAccount.setAvailableBalance(existingAccount.getAvailableBalance().subtract(new BigDecimal(parts[2])));
                        } else {
                            return Mono.error(new IllegalArgumentException("Invalid transaction type: " + parts[1]));
                        }

                        return this.service.update(Mono.just(existingAccount), existingAccount.getId())
                                .onErrorResume(error -> {
                                    log.error("Error updating account: {}", error.getMessage());
                                    return Mono.empty();
                                });
                    } catch (NumberFormatException e) {
                        log.error("Error parsing amount: {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .subscribe();
    }
}
