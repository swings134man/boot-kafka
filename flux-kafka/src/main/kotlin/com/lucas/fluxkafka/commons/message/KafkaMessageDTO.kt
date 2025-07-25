package com.lucas.fluxkafka.commons.message

import java.time.LocalDateTime


data class KafkaMessageDTO(
    val topic: String = "",
    val id: String = "",
    val sender: String = "",
    val message: Any?,
    val timeStamp: LocalDateTime = LocalDateTime.now()
)