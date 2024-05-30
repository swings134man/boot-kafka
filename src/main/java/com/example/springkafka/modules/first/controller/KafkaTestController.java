package com.example.springkafka.modules.first.controller;

import com.example.springkafka.config.kafka.KafkaProducer;
import com.example.springkafka.config.kafka.entity.KafkaMsgDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @name: KafkaTestController
 * @description: Kafka Test Controller - For Pub Test
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaProducer producer;


    @PostMapping(value = "/kafka/test/publish")
    public String sendMessage(@RequestParam String topic , @RequestBody KafkaMsgDTO dto) {
        producer.sendMsg(topic, dto);
        return "Send Success";
    }
}
