package com.example.project02.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청에 대해서
                .allowedOrigins("http://localhost:3000") // 이 오리진에서 오는 요청만 허용
                .allowedOriginPatterns("*") //모든 출처에서의 요청 허용
                .allowedMethods("*") // 모든 HTTP 메소드 허용
                .allowCredentials(true) // 쿠키 전달 허용
                .allowedHeaders("*") // 모든 헤더 허용
                .exposedHeaders("Authorization"); // "Authorization" 헤더 노출
    }
}

