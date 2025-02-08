package com.wbd.ams.producerconsumer.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(Collections.singletonList(
                        new Server().url("/")
                ))
                .info(new Info()
                        .title("Kafka Producer API")
                        .version("1.0.0")
                        .description("API for publishing messages to Kafka topics")
                        .contact(new Contact()
                                .name("Your Company Name")
                                .email("contact@yourcompany.com")
                                .url("https://yourcompany.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public GroupedOpenApi kafkaApi() {
        return GroupedOpenApi.builder()
                .group("kafka-apis")
                .pathsToMatch("/api/kafka/**")
                .build();
    }
}