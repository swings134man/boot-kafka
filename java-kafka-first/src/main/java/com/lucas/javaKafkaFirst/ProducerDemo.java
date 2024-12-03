package com.lucas.javaKafkaFirst;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Properties;


public class ProducerDemo {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());
    private static Properties properties;

    private static void setProperties() {
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092"); // Localhost
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        //security properties for SSL
//        properties.setProperty("security.protocol", "SASL_SSL");
//        properties.setProperty("sasl.mechanism", "PLAIN");
//        properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";");
    }

    public static void main(String[] args) {
        log.info("Starting the Producer Main Class !!");

        // Create Producer Properties
        setProperties();

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // create Producer Record
        ProducerRecord<String, String> record = new ProducerRecord<>("demo_java", "hello world"); //topic: value

        // send Data
        producer.send(record);

        //flush and close producer
        producer.flush(); // 프로듀서에게 전송하고, 완료할 때 까지 대기(동기적) Block - Synchronous
        producer.close(); // 프로듀서를 닫고, 리소스를 해제
    }
}
