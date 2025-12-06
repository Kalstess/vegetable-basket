package com.example.cailanzi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI cailanziOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("菜篮子工程车填报和展示系统 API")
                        .description("基于Spring Boot 3开发的菜篮子工程车数据采集与展示系统后端API")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("技术支持")
                                .email("support@cailanzi.com")));
    }
}