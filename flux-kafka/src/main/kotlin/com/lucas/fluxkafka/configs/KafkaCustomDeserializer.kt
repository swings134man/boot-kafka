package com.lucas.fluxkafka.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lucas.fluxkafka.commons.logger
import com.lucas.fluxkafka.commons.message.KafkaMessageDTO
import org.apache.kafka.common.serialization.Deserializer

/**
 * KafkaCustomDeserializer.kt: Custom Kafka Deserializer Config Class
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 25. 오후 3:29
 * @description: DTO 형식에 맞지 않는 메시지 수신시, null 반환
 * - ObjectMapper 의 경우 LocalDateTime 등을 직렬화/역직렬화 하기 위해 JavaTimeModule 을 등록하고,
 *   WRITE_DATES_AS_TIMESTAMPS 옵션을 비활성화하여 ISO-8601 형식으로 직렬화/역직렬화 한다.(timestamp -> LocalDateTime)
 *
 * - ObjectMapper 가 별도로 Bean 으로 등록되어 있지 않다면, jacksonObjectMapper() 를 통해 커스텀하게 생성한다.
 */
class KafkaCustomDeserializer: Deserializer<KafkaMessageDTO> {
    val objectMapper: ObjectMapper? = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    val logger = logger()

    override fun deserialize(topic: String?, data: ByteArray?): KafkaMessageDTO? {
        return try {
            if (data == null || data.isEmpty()) return null
            objectMapper!!.readValue(data, KafkaMessageDTO::class.java)
        } catch (e: Exception) {
            logger.error("❌ Deserialization error on topic=$topic: ${e.message}")
            null
        }
    }
}