package com.example.cailanzi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                // 注意：由于设置了 context-path=/api，实际路径不包含 /api
                // 所以这里应该匹配所有路径，拦截器内部会处理路径判断
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/company-registration/register",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                );
    }
}

