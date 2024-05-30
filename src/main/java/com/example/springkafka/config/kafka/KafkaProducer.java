package com.example.springkafka.config.kafka;

import com.example.springkafka.config.kafka.entity.KafkaMsgDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @name: Kafka Producer Class
 * @description: Request Transfer to Kafka Broker
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaMsgDTO> kafkaTemplate;

    public void sendMsg(String topic, KafkaMsgDTO dto) {
        log.info("### Producer ### topic = {}, msg = {} ",topic , dto);
        kafkaTemplate.send(topic, dto);
    }
}
