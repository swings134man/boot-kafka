package com.example.springkafka.modules.first.controller;

import com.example.springkafka.modules.first.service.KafkaTestProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaTestProducer producer;


    @PostMapping(value = "/kafka/test/publish")
    public String sendMessage(@RequestParam String message) {
        producer.sendMessage(message);
        return "Send Success";
    }
}
