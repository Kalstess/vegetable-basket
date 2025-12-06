package com.example.cailanzi.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    /**
     * 自定义 LocalDateTime 反序列化器，支持多种格式
     * 支持格式：
     * - ISO 格式：2025-11-15T01:06:00
     * - 空格格式：2025-11-15 01:06:00
     */
    private static class FlexibleLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
        private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        private static final DateTimeFormatter SPACE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        protected FlexibleLocalDateTimeDeserializer() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateTimeStr = p.getText();
            if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
                return null;
            }
            
            // 先尝试 ISO 格式（T 分隔）
            try {
                return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
            } catch (DateTimeParseException e) {
                // 如果失败，尝试空格格式
                try {
                    return LocalDateTime.parse(dateTimeStr, SPACE_FORMATTER);
                } catch (DateTimeParseException e2) {
                    throw new IOException("无法解析日期时间: " + dateTimeStr + "，支持的格式：yyyy-MM-ddTHH:mm:ss 或 yyyy-MM-dd HH:mm:ss", e2);
                }
            }
        }
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 配置 LocalDateTime 序列化器（输出为 ISO 格式）
        javaTimeModule.addSerializer(LocalDateTime.class, 
                new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 配置 LocalDateTime 反序列化器（支持多种格式）
        javaTimeModule.addDeserializer(LocalDateTime.class, 
                new FlexibleLocalDateTimeDeserializer());
        
        return builder
                .modules(javaTimeModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}

