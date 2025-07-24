package com.lucas.fluxkafka

import com.lucas.fluxkafka.configs.properties.KafkaProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(KafkaProperties::class)
class FluxKafkaApplication

fun main(args: Array<String>) {
	runApplication<FluxKafkaApplication>(*args)
}
