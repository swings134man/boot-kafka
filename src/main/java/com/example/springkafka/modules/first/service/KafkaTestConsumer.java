package com.example.springkafka.modules.first.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaTestConsumer {

    @KafkaListener(topics = "exam-topic", groupId = "lucas")
    public void consume(String message) throws Exception{
        log.info(String.format("#### -> Consumed message -> %s", message));
    }

}
