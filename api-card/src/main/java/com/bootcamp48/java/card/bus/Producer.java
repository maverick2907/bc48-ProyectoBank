package com.bootcamp48.java.card.bus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final String TOPIC = "reversion_topic";
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String message) {
        this.kafkaTemplate.send(TOPIC,message);
    }
}
