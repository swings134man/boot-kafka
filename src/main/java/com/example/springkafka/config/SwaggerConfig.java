//package com.example.springkafka.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
///************
// * @info : Swagger Config Class
// * @name : SwaggerConfig
// * @date : 2023/03/29 2:59 AM
// * @author : SeokJun Kang(swings134@gmail.com)
// * @version : 1.0.0
// * @Description :
// ************/
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig implements WebMvcConfigurer {
//
//    //http://localhost:8080/custom/swagger-ui/index.html#/
//    //http://localhost:8080//v2/api-docs?group=boot-kafka
//
//    private static final String SERVICE_NAME = "boot-kafka Project";
//    private static final String API_VERSION = "V1";
//    private static final String API_DESCRIPTION = "boot-kafka API TEST";
//    private static final String API_URL = "http://localhost:8080/";
//
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo()).groupName("boot-kafka")
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build()
//                .pathMapping("/custom");
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder().title(SERVICE_NAME) // 서비스명
//                .version(API_VERSION)                   // API 버전
//                .description(API_DESCRIPTION)           // API 설명
//                .termsOfServiceUrl(API_URL)             // 서비스 url
//                .licenseUrl("https://github.com/swings134man/boot-kafka")
//                .build();
//    }// API INFO
//
//
//
//}
