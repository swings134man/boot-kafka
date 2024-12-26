package com.test;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
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
 * - 동일한 Message 를 2번 읽게 될 경우 멱등성에 위배되기때문에 멱등성 설정을 해줘야함.
 *  - Opensearch 에 2번 insert 됨.
 *  - 전략은 2개 -> kafka record 좌표를 사용하여 ID 를 생성하거나(opensearch 에 ID 로 사용함, 카프카 토픽,파티션,오프셋으로 ID 생성),
 *  - 데이터 자체가 ID 를 가지고 있을 경우 -> 데이터의 ID 를 사용함.
 *
 *  - kafka Commit 전략
 *    - 기본적으로 auto.commit.enable = true, auto.commit.interval.ms = 5000ms(5초) 이다.
 *    - 수동 커밋 옵션을 사용하면, 커밋이 되지 않았기 떄문에 처음부터 다시 읽을 수 있다.
 *    - 코드를 추가해서 수동 커밋을 해줘야 한다. -> consumer.commitSync();
 *    - 이때 기본적으로 at least once(최소한 한번) 이고, 읽은 데이터 처리 완료 이후 commit 하게 된다.(batch 처럼 동작하게 됨)
 *
 *  - Bulk Request(대량 요청)
 *      - 데이터 하나씩 보내는 것이 아닌, 대량으로 보내는 방법
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
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Manual Commit(수동 커밋)

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

    private static String extractId(String value) {
        return JsonParser.parseString(value)
                .getAsJsonObject()
                .get("meta")
                .getAsJsonObject()
                .get("id")
                .getAsString();
    }

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());

        // 1. create Opensearch Client
        RestHighLevelClient openSearchClient = createOpenSearchClient();

        // 2. create Kafka Consumer
        KafkaConsumer<String, String> consumer = createKafkaConsumer();

        // Main Thread Shutdown Signal With graceful Shutdown
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                // 종료 Singal 이 발생했을때 해당 Thread 가 실행되고, mainThread 를 종료시킴.
                logger.info("Detected ShutDown Hook !!");

                consumer.wakeup();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


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

                // Bulk Request(대량 요청)
                BulkRequest bulkRequest = new BulkRequest();

                // 4. Insert Data(OpenSearch) send
                for (ConsumerRecord<String,String> record: records){
                    // OpenSearch Index Exception Handling
                    try {
                        // 데이터의 ID 값으로 ID 를 설정. -> 중복데이터 방지(ES 에선 덮어씌움)
                        String id = extractId(record.value());
                        IndexRequest indexRequest = new IndexRequest("wikimedia")
                                .source(record.value(), XContentType.JSON)
                                .id(id);

                        // Bulk Request
                        bulkRequest.add(indexRequest);

//                        IndexResponse response = openSearchClient.index(indexRequest, RequestOptions.DEFAULT); // 1건씩 요청 보낼떄 사용함.
//                        logger.info(response.getId()); // opensearch docs id
                    }catch (Exception e) {
                    }
                }

                if(bulkRequest.numberOfActions() > 0) {
                    BulkResponse bulkResponse = openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);// 대량 요청
                    logger.info("Inserted : " + bulkResponse.getItems().length + " records");

                    // 대량 작업 사용 효율을 높이기 위한 sleep
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 5. Commit the offset: bulk insert 성공시 commit
                    consumer.commitSync();
                    logger.info("Offsets have been committed");
                }// if

//                // 5. Commit the offset : 1건씩 요청보낼때 사용함
//                consumer.commitSync();
//                logger.info("Offsets have been committed");
            }
        // try/catch For graceful Shutdown
        }catch (WakeupException e){
            logger.info("Consumer is starting to Shutdown !!");
        }catch (Exception e){
            logger.error("Unexpected Exception in the Consumer: ", e);
        }finally {
            consumer.close(); // Consumer Close, Commit offsets
            openSearchClient.close();
            logger.info("Consumer is Closed !!");
        }
    }//main
}
