package com.example.springkafka.config.kafka.group;

import com.example.springkafka.config.kafka.entity.KafkaMsgDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @name: KafkaExamConsumer Listener
 * @description: exam-topic Subscribe Consumer Class
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaExamConsumer {

    @KafkaListener(topics = "exam-topic", groupId = "lucas")
    public void examTopic(KafkaMsgDTO dto) {
        log.info("### Consumer ### exam-topic = {}", dto);
    }

}
