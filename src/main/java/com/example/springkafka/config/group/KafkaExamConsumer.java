package com.example.springkafka.config.group;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaExamConsumer {

    @KafkaListener(topics = "exam-topic", groupId = "lucas")
    public void examTopic(String msg) {
        log.info("### Consumer ### exam-topic = {}", msg);
    }

}
