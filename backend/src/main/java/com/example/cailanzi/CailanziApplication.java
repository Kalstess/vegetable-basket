package com.example.cailanzi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CailanziApplication {
    public static void main(String[] args) {
        SpringApplication.run(CailanziApplication.class, args);
    }
}