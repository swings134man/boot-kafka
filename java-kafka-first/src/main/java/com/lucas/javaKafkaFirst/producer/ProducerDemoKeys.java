package com.lucas.javaKafkaFirst.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * ProducerDemoKeys
 * - Key 를 정의하고 send()
 */
public class ProducerDemoKeys {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoKeys.class.getSimpleName());
    private static Properties properties;

    private static void setProperties() {
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092"); // Localhost
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
    }

    public static void main(String[] args) throws InterruptedException {
        log.info("Starting the Producer With CallBack !!");

        setProperties();

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // 2 * 10
        for (int j = 0; j < 2; j++) {
            // send Data
            for (int i = 0; i < 10; i++) {
                //
                String topic = "demo_java";
                String key = "id_" + i;
                String value = "hello world " + i;

                ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        // e == null : No Exception
                        if (e == null) {
                            log.info("key: " + key + " | Partition: " + recordMetadata.partition());
                        } else {
                            log.error("Error while producing", e);
                        }
                    }
                });
            }
            Thread.sleep(500);
        }


        producer.flush();
        producer.close();
    }
}
