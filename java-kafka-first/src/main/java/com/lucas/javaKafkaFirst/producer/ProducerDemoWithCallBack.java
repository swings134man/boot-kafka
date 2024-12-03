package com.lucas.javaKafkaFirst.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * Producer with CallBack
 * send() 에 인자로 Callback을 넘겨주면, 메시지 전송이 성공 + 예외가 없는경우 onCompletion() 메소드가 호출된다.
 *
 * RoundRobin 방식이 아닌, Sticky Partitioning 방식으로 메시지를 전송한다.
 *
 */
public class ProducerDemoWithCallBack {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallBack.class.getSimpleName());
    private static Properties properties;

    private static void setProperties() {
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092"); // Localhost
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
//        properties.setProperty("batch.size", "400"); // 한번처리할 Size default: 16kb (16 * 1024)
//        properties.setProperty("partitioner.class", RoundRobinPartitioner.class.getName());// RoundRobin 방식
    }

    public static void main(String[] args) {
        log.info("Starting the Producer With CallBack !!");

        setProperties();

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        ProducerRecord<String, String> record = new ProducerRecord<>("demo_java", "hello world"); //topic: value

        // send() 를 for() 안에서 호출
//        for (int i = 0; i < 30; i++) {
//
//        }
        
        // send Data
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // e == null : No Exception
                if (e == null) {
                    log.info("Received new Metadata. \n" +
                            "Topic: " + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());
                } else {
                    log.error("Error while producing", e);
                }
            }
        });

        producer.flush();
        producer.close();
    }
}
