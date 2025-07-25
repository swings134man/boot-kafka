package com.lucas.fluxkafka.commons.message

import com.lucas.fluxkafka.commons.logger
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord

/**
 * KafkaSender.kt: Reactor Kafka Sender(Producer) Service
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 24. 오후 4:21
 * @description: 
 */
@Service
class KafkaSenderService(
    private val kafkaSender: KafkaSender<String, KafkaMessageDTO>
) {

    val logger = logger()

    suspend fun sendMessage(topic: String, message: KafkaMessageDTO) {
        val senderRecord = SenderRecord.create(
            ProducerRecord(topic, message.sender, message),
            null
        )

        kafkaSender.send(Flux.just(senderRecord))
            .next()
            .doOnSuccess { logger.info("✅ Sent to $topic: ${message.message}") }
            .doOnError { logger.info("❌ Failed to send: ${it.message}") }
            .awaitFirstOrNull()
    }

}