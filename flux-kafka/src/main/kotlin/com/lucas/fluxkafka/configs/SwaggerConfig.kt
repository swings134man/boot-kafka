package com.lucas.fluxkafka.configs

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * SwaggerConfig.kt: Swagger Reactor UI
 *
 * @author: lucaskang(swings134man)
 * @since: 2025. 7. 23. 오후 5:28
 * @description:
 */
@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(configurationInfo())
    }

    private fun configurationInfo(): Info {
        return Info()
            .title("flux-kafka API's")
            .description("kotlin & Coroutine - kafka Application")
            .version("1.0.0")
    }
}