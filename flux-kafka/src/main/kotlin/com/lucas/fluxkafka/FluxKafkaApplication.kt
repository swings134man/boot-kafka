package com.lucas.fluxkafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FluxKafkaApplication

fun main(args: Array<String>) {
	runApplication<FluxKafkaApplication>(*args)
}
