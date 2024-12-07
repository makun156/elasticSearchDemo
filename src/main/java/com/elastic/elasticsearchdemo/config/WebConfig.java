package com.elastic.elasticsearchdemo.config;

import com.elastic.elasticsearchdemo.intercept.GlobalInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public String[] excludePath={
            "/api/user/register","/api/user/login"
    };
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalInterceptor()).excludePathPatterns(excludePath);
    }
}
