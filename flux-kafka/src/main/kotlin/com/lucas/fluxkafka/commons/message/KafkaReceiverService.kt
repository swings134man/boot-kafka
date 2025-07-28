package com.lucas.fluxkafka.commons.message

import com.lucas.fluxkafka.commons.logger
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.util.concurrent.ConcurrentHashMap

/**
 * KafkaReceiver.kt: Reactor Kafka Receiver(Consumer) Service
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 24. 오후 4:21
 * @description: 에러 발생시, 해당 메시지 무시 및 구독유지
 * - Deserializer 에서 null 반환된 경우, 해당 메시지 필터링 사용자에게 전달하지 않음
 */
@Service
class KafkaReceiverService(
    private val receiverOption: ReceiverOptions<String, KafkaMessageDTO>
) {
    val logger = logger()
    private val topicFluxMap = ConcurrentHashMap<String, Flux<KafkaMessageDTO>>()

    /**
     * @name: consume
     * @author: lucaskang(swings134man)
     * @since: 2025. 7. 24. 오후 6:11
     * @description: 특정 topic 을 구독(subscribe) 하고, 해당 topic 의 메시지를 Consume 한다.
     * - 특정 topic 을 구독중이지 않다면, 새로운 구독 생성 후 Flux 반환
     * - 이미 존재하는(구독중) 이라면, 해당 topic 의 Flux(사용중인) 반환
     * - 구독중인 topic 즉 flux 는 최소 1명 이상이 구독중일 때 유지됨
     * - 또한 ConcurrentHashMap 을 사용하여 멀티스레드 환경에서도 안전하게 topic 별 Flux 를 관리한다.
     * - 순서가 보장되지 않음(유의)
     * - 순서 보장이 필요할 경우 -> concatMap() 사용 -> 속도 저하 주의
     */
    fun consume(topic: String): Flux<KafkaMessageDTO> {
        return topicFluxMap.computeIfAbsent(topic) {
            val options = receiverOption.subscription(listOf(topic))
            KafkaReceiver.create(options)
                .receive()
                .flatMap({ record ->
                    val value = record.value()

                    if (value == null) {
                        Mono.empty()
                    } else {
                        Mono.just(value)
                            .doOnSuccess {
                                // 메시지 정상 처리된 경우에만 offset 커밋
                                record.receiverOffset().acknowledge()
                                logger.info("✅ Commit offset ${record.offset()} 완료")
                            }
                    }
                }, 8) // 병렬 처리 갯수 설정시 ({}, n: Int) 설정 할것 없으면 무한
                .onErrorContinue { error, item ->
                    logger.error("Kafka Consume Error: ${error.message} -- Item: $item")
                }
                .publish()
                .refCount(1)
        }
    }


    // 병렬동작시, 커밋 문제 발생하는 코드(수정전): 메시지가 처리되지 않았는데도 커밋됨
    fun consumeExampleBeforeFix(topic: String): Flux<KafkaMessageDTO> {
        return topicFluxMap.computeIfAbsent(topic) {
            val options = receiverOption.subscription(listOf(topic))
            KafkaReceiver.create(options)
                .receive()
                .doOnSubscribe { logger.info("🎧 Subscribed to topic: $topic") }
//                .doOnNext { it.receiverOffset().acknowledge() } // 수동커밋 -> map 뒤에선 동작 안함.
                .map { it.value() }
                .filter { it != null } // null 값 필터링, Custom Deserializer 에서 null 반환된 경우
                .onErrorContinue { error, item ->
                    logger.error("Kafka Consume Error: ${error.message} -- Item: $item") // 에러 발생시 로그 출력, 처리안하면 구독취소됨. 여기선 에러발생시 무시하고 진행
                }
                .publish()
                .refCount(1) // 최소 1명부터 연결 유지
        }
    }

}