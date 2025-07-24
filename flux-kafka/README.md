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

---
### TODO LIST 
- 2025.07.24 
  - pub/sub 기본적인 테스트는 완료 
  - 다만 여러 클라이언트의 Consume시 : Rebalancing 이 발생하며, 기존 커넥션된 컨슈머, 새로 유입되는 유저 모두 오류가 발생함(무한루프 나다가 오류) 