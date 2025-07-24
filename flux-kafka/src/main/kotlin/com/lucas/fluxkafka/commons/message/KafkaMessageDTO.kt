package com.lucas.fluxkafka.commons.message


data class KafkaMessageDTO(
    val topic: String = "",
    val id: String = "",
    val sender: String = "",
    val message: Any?,
    val timeStamp: Long = System.currentTimeMillis(),
)