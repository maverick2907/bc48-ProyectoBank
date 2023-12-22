package com.bootcamp48.java.card.bus;

import com.bootcamp48.java.card.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class Consumer {
    @Autowired
    private CardService service;
    @Autowired
    private Producer producer;

    @KafkaListener(topics = "operation_topic",groupId = "group_id")
    public void consumeMessage(String message) {
        log.info("Consuming Operation with IDs {}.", message);
        var parts = message.split("\\|");
        var customers = parts[1];
        List<String> list = Arrays.asList(customers.substring(1, customers.length() - 1).split(", "));
        AtomicBoolean valid = new AtomicBoolean(true);
        Flux.fromIterable(list)
                .flatMap(id -> service.validDebt(id).doOnNext(b -> {
                    if (!b && valid.get()) {
                        this.producer.sendMessage(parts[0]);
                        valid.set(false);
                    }
                })).subscribe();
    }
}
