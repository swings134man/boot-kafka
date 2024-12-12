package com.lucas.kafka.wikimedia;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Wikimedia Kafka Producer Main
 */
public class WikimediaChangesProducer {
    private static final Logger log = LoggerFactory.getLogger(WikimediaChangesProducer.class.getSimpleName());
    private static Properties properties;

    // Kafka Properties
    private static void setProperties() {
        properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); // Localhost
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // safe Producer Config (Kafka Version <= 2.8) -> Kafka 2.8 이상은 Default
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // -1
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE)); // -1

        // Batch Config & Compression
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20"); // 20ms 대기
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024)); // 32KB
    }

    // Event Handler
    private static void startEventSource(KafkaProducer<String, String> producer, String topic) {
        // Event Handler: Stream 에서 오는 Event 처리 -> Kafka Producer 로 전송
        EventHandler eventHandler = new WikimediaChangeHandler(producer, topic);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();

        // start the producer in another thread: 독립된 Thread 에서 실행됨
        eventSource.start();
    }

    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "localhost:9092";

        // create Producer properties
        setProperties();

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // Start Event Source
        startEventSource(producer, "wikimedia.recentchange");

        // produce until 10min and block the Main Thread: 10분동안 Main Thread 를 Block -> 다른 Thread 가 Kafka Producer 진행.
        TimeUnit.MINUTES.sleep(10);

    }
}
