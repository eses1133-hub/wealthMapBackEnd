package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允許所有 API 路徑
                .allowedOrigins(
                    "https://wealthmap-roooooong.netlify.app",       // 你的前端網址
                    "https://wealthmap-jingyi-20260514.netlify.app" // 夥伴的前端網址（新加入）
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 如果有使用 Cookie 或 Auth 標頭需要開啟
    }

}
