package com.test;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @package : com.test
 * @name : OpenSearchConsumer.java
 * @date : 2024. 12. 23. 오후 11:30
 * @author : lucaskang(swings134man)
 * @Description: OpenSearchConsumer Kafka
**/
public class OpenSearchConsumer {

    private static final String GROUP_ID = "consumer-opensearch-demo";
    private static Properties properties;

    private static KafkaConsumer<String, String> createKafkaConsumer() {
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092"); // Localhost
        properties.setProperty("key.deserializer", StringDeserializer.class.getName()); // Deserializer
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());
        properties.setProperty("group.id", GROUP_ID); // Consumer Group ID
        properties.setProperty("auto.offset.reset", "latest"); // none, earliest, latest:

        return new KafkaConsumer<>(properties);
    }

    private static RestHighLevelClient createOpenSearchClient() {
        String connString = "http://localhost:9200";

        RestHighLevelClient restHighLevelClient;
        URI connUri = URI.create(connString);
        // login Info If Exists
        String userInfo = connUri.getUserInfo();

        if(userInfo == null) {
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), connUri.getScheme())));
        }else {
            // REST Client With Security
            String[] auth = userInfo.split(":");

            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), connUri.getScheme()))
                            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(cp)
                                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                            ));
        }
        return restHighLevelClient;
    }

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());

        // 1. create Opensearch Client
        RestHighLevelClient openSearchClient = createOpenSearchClient();

        // 2. create Kafka Consumer
        KafkaConsumer<String, String> consumer = createKafkaConsumer();


        // 1-1 Create the index on OpenSearch if it doesn't exist(인덱스가 없다면 생성)
        // 요청이 성공,실패 어떤 경우든 close() 됨. -> 예외가 발생했을때 둘다 닫히게 함.
        try(openSearchClient; consumer){
            boolean indexExists = openSearchClient.indices().exists(new GetIndexRequest("wikimedia"), RequestOptions.DEFAULT);

            if(!indexExists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest("wikimedia"); // index Name
                openSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                logger.info("WikiMedia Index Created");
            }else {
                logger.info("WikiMedia Index Already Exists");
            }

            // 3. Consume Data(Kafka)
            consumer.subscribe(Collections.singleton("wikimedia.recentchange")); // subscribe to a topic
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000)); // 3s: 데이터가 없을경우 3초 기다림.

                int recordCount = records.count();
                logger.info("Received " + recordCount + " records");

                // 4. Insert Data(OpenSearch) send
                for (ConsumerRecord<String,String> record: records){
                    // OpenSearch Index Exception Handling
                    try {
                        IndexRequest indexRequest = new IndexRequest("wikimedia")
                                .source(record.value(), XContentType.JSON);

                        IndexResponse response = openSearchClient.index(indexRequest, RequestOptions.DEFAULT);
                        logger.info(response.getId()); // opensearch docs id
                    }catch (Exception e) {
                    }

                }
            }
        }
    }//main
}
