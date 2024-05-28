package com.example.springkafka.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMsg(String topic, String message) {
        log.info("### Producer ### topic = {}, msg = {} ",topic , message);
        kafkaTemplate.send(topic, message);
    }
}
