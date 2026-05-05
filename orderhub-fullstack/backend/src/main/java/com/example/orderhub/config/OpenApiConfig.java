package com.example.orderhub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Order Hub API")
                .version("1.0")
                .description("API de demonstração para entrevista full stack")
                .contact(new Contact().name("Wallace Monteiro").email("wallace@example.com")));
    }
}
