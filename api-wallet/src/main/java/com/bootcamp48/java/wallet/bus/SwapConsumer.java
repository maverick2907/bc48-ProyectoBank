package com.bootcamp48.java.wallet.bus;

import com.bootcamp48.java.swap.event.Event;
import com.bootcamp48.java.wallet.model.Swap;
import com.bootcamp48.java.wallet.service.WalletService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SwapConsumer {
    @Autowired
    private WalletService service;

    @KafkaListener(topics = "${kafka.topic.swap.name}", containerFactory = "kafkaListenerContainerFactory", groupId = "group_id")
    public void consumer(Event<Swap> event) {
        log.info("Message received : {} ", event);
        ObjectMapper mapper = new ObjectMapper();
        Swap swap = mapper.convertValue(event.getData(), new TypeReference<>() {});
        service.validBySwap(swap).subscribe();
    }
}
