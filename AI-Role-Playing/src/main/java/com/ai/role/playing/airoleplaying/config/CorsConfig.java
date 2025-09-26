package com.ai.role.playing.airoleplaying.config;

// 包路径示例：com.example.demo.config
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 允许所有/api/**路径的接口跨域
                .allowedOrigins("http://localhost:5173")  // 前端实际地址（必须精确匹配）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 包含OPTIONS预检请求
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许携带Cookie
                .maxAge(3600);  // 预检请求缓存时间
    }
}
