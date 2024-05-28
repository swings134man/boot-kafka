package com.example.springkafka.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // http://localhost:8080/swagger-ui/index.html

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Spring Kafka Application")
                        .version("1.0.0")
                        .description("Spring Kafka Test Application"));
        // If you Use JWT Token for Authorization With login
//                .components(new Components()
//                        .addSecuritySchemes("bearer-key", new SecurityScheme()
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("bearer")
//                                .bearerFormat("JWT")));
    }
}
