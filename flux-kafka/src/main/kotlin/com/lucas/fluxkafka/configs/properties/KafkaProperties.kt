package com.lucas.fluxkafka.configs.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.kafka")
data class KafkaProperties(
    val bootstrapServers: String,
    val consumer: ConsumerProperties,
    val producer: ProducerProperties

) {
    data class ConsumerProperties(
        val groupId: String,
        val autoOffsetReset: String,
    )

    data class ProducerProperties(
        val acks: String,
        val retries: Int
    )
}
