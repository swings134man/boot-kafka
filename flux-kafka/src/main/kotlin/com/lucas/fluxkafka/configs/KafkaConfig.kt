package com.lucas.fluxkafka.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lucas.fluxkafka.commons.message.KafkaMessageDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
//import org.springframework.kafka.support.serializer.JsonDeserializer
//import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import kotlin.jvm.java

/**
 * KafkaConfig.kt: Reactor Kafka Configuration
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 24. 오후 4:17
 * @description: 
 */
@Configuration
class KafkaConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var hosts: String

    @Value("\${spring.kafka.consumer.group-id}")
    lateinit var groupId: String

    @Value("\${spring.kafka.consumer.auto-offset-reset}")
    lateinit var autoOffsetReset: String


    // ------------------ Consumer Configuration ------------------
    @Bean
    fun receiverOptions(objectMapper: ObjectMapper): ReceiverOptions<String, KafkaMessageDTO> {
        val properties = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to hosts,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to true, // auto commit(자동 커밋)
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to autoOffsetReset, // latest, earliest, none (어디부터 읽을지)
            JsonDeserializer.TYPE_MAPPINGS to "kafkaMessage:com.lucas.fluxkafka.commons.message.KafkaMessageDTO",
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )

        return ReceiverOptions.create<String, KafkaMessageDTO>(properties)
            .withValueDeserializer(JsonDeserializer(KafkaMessageDTO::class.java, objectMapper))
    }


    // ------------------ Producer Configuration ------------------
    @Bean
    fun kafkaSender(objectMapper: ObjectMapper): KafkaSender<String, KafkaMessageDTO> {
        val properties = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to hosts,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "all",
            ProducerConfig.RETRIES_CONFIG to 3,
            JsonSerializer.TYPE_MAPPINGS to "kafkaMessage:com.lucas.fluxkafka.commons.message.KafkaMessageDTO"
        )

        val senderOptions = SenderOptions.create<String, KafkaMessageDTO>(properties)
            .withValueSerializer(JsonSerializer(objectMapper))

        return KafkaSender.create(senderOptions)
    }


}