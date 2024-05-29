package com.example.springkafka.modules.first.controller;

import com.example.springkafka.config.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaProducer producer;


    @PostMapping(value = "/kafka/test/publish")
    public String sendMessage(@RequestParam String topic ,@RequestParam String message) {
        producer.sendMsg(topic, message);
        return "Send Success";
    }
}
