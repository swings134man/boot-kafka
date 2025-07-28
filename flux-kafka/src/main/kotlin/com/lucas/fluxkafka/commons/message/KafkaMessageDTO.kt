package com.lucas.fluxkafka.commons.message

import java.time.LocalDateTime
import java.util.UUID

/**
 * KafkaMessageDTO.kt: Kafka Message Data Object
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 29. 오전 2:06
 * @description: Key 값을 사용처에 따라 제대로 설정할것.
 * - 의미있는(userId, OrderId, 각종 id, traceId) 와 같은것을 key 로 설정할것.
 * - topic, sender, id 필드의 경우 추적용
 */
data class KafkaMessageDTO(
    val topic: String = "", // 메시지 발송 대상 topic
    val key: String = "", // 메시지 키(같은 키면 같은파티션 그룹핑, 파티션 지정 및 순서보장)
    val id: String = UUID.randomUUID().toString(), // Random 고유 식별자
    val sender: String = "", // 발신자 정보(서버 name, 사용자 ID 등)
    val message: Any?,
    val timeStamp: LocalDateTime = LocalDateTime.now()
)