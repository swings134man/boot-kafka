# flux-kafka

- Kotlin Coroutine + Spring WebFlux + Kafka 샘플 모듈

- Kafka 는 홈서버의 Single Broker 환경으로 테스트중
- Kafka = KRaft mode


---

### Stack

1. Spring boot (WebFlux) 3.4.7
2. Kotlin 1.9.25
3. Kafka : bitnami/kafka:3.6 : KRaft Mode (Single Broker)


---

### Info 

- 테스트용 Kafka Broker 사용 
- `io.projectreactor.kafka:reactor-kafka` 사용으로 non-blocking kafka 사용
  - 이걸 사용하지 않고, `spring-kafka` 를 사용하면 blocking 방식으로 동작함
  - 배압 조절도 가능?