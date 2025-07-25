package com.lucas.fluxkafka.commons.message

import java.time.LocalDateTime
import java.util.UUID


data class KafkaMessageDTO(
    val topic: String = "",
    val id: String = UUID.randomUUID().toString(),
    val sender: String = "",
    val message: Any?,
    val timeStamp: LocalDateTime = LocalDateTime.now()
)