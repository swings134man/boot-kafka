# flux-kafka

- Kotlin Coroutine + Spring WebFlux + Kafka 샘플 모듈

- Kafka 는 홈서버의 Single Broker 환경으로 테스트중
- Kafka = KRaft mode


---

### Stack

1. Spring boot (WebFlux) 3.4.7
2. Kotlin 1.9.25
3. Kafka : bitnami/kafka:3.6 : KRaft Mode (Single Broker) : 테스트용 Kafka Broker 사용


---

### Info 

#### Reactor Kafka
- `io.projectreactor.kafka:reactor-kafka` 사용으로 non-blocking kafka 사용
  - 이걸 사용하지 않고, `spring-kafka` 를 사용하면 blocking 방식으로 동작함
    - 해당 library 는 내부적으로 `sender/receiver` 를 사용함.
  - 배압 조절도 가능?

<br/><br/>


- 공식문서 번역본 참고
> - https://godekdls.github.io/Reactor%20Kafka/whatsnewinreactorkafka120release/

---
### Functional List (기능 리스트) 

- SSE 기능 `(/api/kafka/stream/{topic-name})`
  - Kafka Topic 에서 발생하는 이벤트를 SSE 로 전달
  - `Flux` 를 사용하여 non-blocking 방식으로 처리
  - `KafkaReceiverService` 의 consume() 을 통하여 Topic 을 구독.
    - 구독한 Flux 객체는 ConcurrentHashMap 에 저장되며, Topic 별로 상태관리
      - topic 구독을 하지않았으면, map 에 추가하고 flux 객체를 반환 -> 모든 사용자가 하나의 flux 를 공유
      - 구독상태면 map 안에 있는 flux 객체를 반환
    - 구독 취소시, 해당 Flux 객체를 제거 만약 클라이언트가 1명미만이라면 구독 종료 옵션 존재함
> curl -N http://localhost:8080/api/kafka/stream/{topic-name}


---
## ETC INFO 

- `Reactor Kafka` 에서는 기본적으로 auto commit =true 를 권장하지 않음 그래서 false 로 강제해놨음
  - webflux 에서 사용시, auto commit 을 사용하면, 메세지를 처리하기 전에 offset 이 커밋되어 버리는 문제가 발생하기 때문
  - https://github.com/reactor/reactor-kafka/issues/326 참고문서