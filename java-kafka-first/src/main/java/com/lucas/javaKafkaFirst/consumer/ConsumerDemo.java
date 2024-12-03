package com.lucas.javaKafkaFirst.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;


/**
 * Consumer poll() Demo
 *
 * 1. auto.offset.reset: none, earliest, latest
 *  - none: 컨슈머 그룹이 없을때 동작하지 않는다.
 *  - earliest: 가장 처음부터 Topic 메시지를 가져온다.
 *  - latest: 지금부터 메시지를 읽을것이다. 새로운 메시지만 읽음.
 *
 * 그룹에 Join 하는동안엔 데이터를 읽지못함. 그룹에 Join이 완료되면 데이터를 읽을 수 있음.
 * -> 그룹 Offset 을 0 으로 설정한 이후부터 처음부터 읽을 수 있음.
 *
 * 프로그램을 바로 종료 후 다시 실행 하면 시간이 오래걸림
 * -> 프로그램을 안전하게 종료하지 않았기 때문에, Group RE-Joining 이 발생하고, offset 을 다시 찾아야 하기 때문임.
 */
public class ConsumerDemo {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class.getSimpleName());
    private static final String GROUP_ID = "my-first-application";
    private static final String TOPIC = "demo_java";
    private static Properties properties;

    private static void setProperties() {
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092"); // Localhost
        properties.setProperty("key.deserializer", StringDeserializer.class.getName()); // Deserializer
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());

        properties.setProperty("group.id", GROUP_ID); // Consumer Group ID
        properties.setProperty("auto.offset.reset", "earliest"); // none, earliest, latest:
    }

    public static void main(String[] args) {
        log.info("Starting the Consumer Main Class !!");

        //setProperties
        setProperties();

        // Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // subscribe to a topic
        consumer.subscribe(Arrays.asList(TOPIC));

        // poll for data
        while (true) {
            log.info("Polling");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000)); // 얼마나 기다릴건지. 1s: 데이터가 없을경우 1초 기다림.

            // print the records
            records.forEach(record -> {
                log.info("Key: " + record.key() + ", Value: " + record.value());
                log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
            });
        }

    }
}
