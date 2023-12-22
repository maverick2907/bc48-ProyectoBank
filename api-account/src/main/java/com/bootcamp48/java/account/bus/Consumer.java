package com.bootcamp48.java.account.bus;

import com.bootcamp48.java.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class Consumer {

    private final AccountService service;

    @KafkaListener(topics = "reversion_topic",groupId = "group_id")
    public void consumeMessage(String message) {
        log.info("Consuming Reversion with ID {}.", message);
        service.deleteById(message).subscribe();
    }
}
