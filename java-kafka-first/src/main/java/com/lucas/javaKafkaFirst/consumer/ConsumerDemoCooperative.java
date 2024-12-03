package com.lucas.javaKafkaFirst.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;


/**
 * 리밸런싱 - 협력적 전략
 */
public class ConsumerDemoCooperative {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemoCooperative.class.getSimpleName());
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
        properties.setProperty("partition.assignment.strategy", CooperativeStickyAssignor.class.getName()); // 협력적 전략
//        properties.setProperty("group.instance.id", ""); // 나중에 추가예정(static Member)
    }

    public static void main(String[] args) {
        log.info("Starting the Consumer(ShutDown) Main Class !!");

        //setProperties
        setProperties();

        // Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // Main Thread 정보를 얻어야함.
        final Thread mainThread = Thread.currentThread();

        // ShutDown Hook 을 추가해야함
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                // 종료 Singal 이 발생했을때 해당 Thread 가 실행되고, mainThread 를 종료시킴.
                log.info("Detected ShutDown Hook !!");

                consumer.wakeup(); // 다음 poll() 실행시 wakeup Exception을 발생시킴. 이걸 Catch 하여 사용.
                // join the Main Thread and Main Thread 의 execution 을 허용함.
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            // subscribe to a topic
            consumer.subscribe(Arrays.asList(TOPIC));

            // poll for data
            while (true) {
//                log.info("Polling");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000)); // 얼마나 기다릴건지. 1s: 데이터가 없을경우 1초 기다림.

                // print the records
                records.forEach(record -> {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                });
            }
        }catch (WakeupException e){
            log.info("Consumer is starting to Shutdown !!");
        }catch (Exception e){
            log.error("Unexpected Exception in the Consumer: ", e);
        }finally {
            consumer.close(); // Consumer Close, Commit offsets
            log.info("Consumer is Closed !!");
        }


    }
}
