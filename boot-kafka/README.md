# boot-kafka

## 📖About
- This Project Is Test & Study For Kafka 
- Make Good Construction And For Using work
- Aim For scalable and usable Code

### 🔥Skills
- Spring Boot 3.2.6
- Java 17 
- docker
  - Kafka 2.8.0
  - Zookeeper
- MariaDB
- Swagger - Spring Doc(For Boot 3.x)

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