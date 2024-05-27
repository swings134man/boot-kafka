# boot-kafka

### Kafka Command
```shell

# Create Topic
kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic exam-topic


# pub
kafka-console-producer.sh --topic exam-topic --broker-list localhost:9092
## After Messages 

# sub - Terminal
kafka-console-consumer.sh --topic exam-topic --bootstrap-server localhost:9092 --from-beginning

```