package com.lucas.fluxkafka.rest

import com.lucas.fluxkafka.commons.message.KafkaMessageDTO
import com.lucas.fluxkafka.commons.message.KafkaReceiverService
import com.lucas.fluxkafka.commons.message.KafkaSenderService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration

@RestController
@RequestMapping("/api/kafka")
class KafkaController(
    private val senderService: KafkaSenderService,
    private val receiverService: KafkaReceiverService
) {

    @PostMapping("/send/{topic}")
    suspend fun sendMessage(
        @PathVariable topic: String,
        @RequestBody message: KafkaMessageDTO
    ): ResponseEntity<String> {
        senderService.sendMessage(topic, message)
        return ResponseEntity.ok("Message sent to $topic")
    }

    // SSE
    @GetMapping("/stream/{topic}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamMessages(@PathVariable topic: String): Flux<ServerSentEvent<KafkaMessageDTO>> {
        return receiverService.consume(topic)
            .map {
                ServerSentEvent.builder(it).build()
            }
    }

}