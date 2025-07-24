package com.lucas.fluxkafka.commons.message

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

/**
 * KafkaReceiver.kt: Reactor Kafka Receiver(Consumer) Service
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 24. 오후 4:21
 * @description: 
 */
@Service
class KafkaReceiverService(
    private val receiverOption: ReceiverOptions<String, KafkaMessageDTO>
) {

    fun consume(topic: String): Flux<KafkaMessageDTO> {
        val options = receiverOption.subscription(listOf(topic))
        return KafkaReceiver.create(options)
            .receive()
            .doOnSubscribe { println("🎧 Subscribed to topic: $topic") }
            .map { it.value() }
    }


}