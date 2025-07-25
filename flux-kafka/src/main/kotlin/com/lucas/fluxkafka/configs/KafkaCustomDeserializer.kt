package com.lucas.fluxkafka.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.lucas.fluxkafka.commons.logger
import com.lucas.fluxkafka.commons.message.KafkaMessageDTO
import org.apache.kafka.common.serialization.Deserializer

/**
 * KafkaCustomDeserializer.kt: Custom Kafka Deserializer Config Class
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 25. 오후 3:29
 * @description: DTO 형식에 맞지 않는 메시지 수신시, null 반환
 */
class KafkaCustomDeserializer: Deserializer<KafkaMessageDTO> {
    private val objectMapper = ObjectMapper()
    val logger = logger()

    override fun deserialize(topic: String?, data: ByteArray?): KafkaMessageDTO? {
        return try {
            if (data == null || data.isEmpty()) return null
            objectMapper.readValue(data, KafkaMessageDTO::class.java)
        } catch (e: Exception) {
            logger.error("❌ Deserialization error on topic=$topic: ${e.message}")
            null
        }
    }
}