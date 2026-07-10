package com.example.authservice.config;

import com.example.authservice.security.JwtAuthFilter;
import com.example.authservice.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    /**
     * Registers the JWT filter only on protected routes. Public auth endpoints
     * (register/login/refresh) and the H2 console are intentionally excluded.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(JwtService jwtService, ObjectMapper objectMapper) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthFilter(jwtService, objectMapper));
        registration.addUrlPatterns("/api/users/*", "/api/admin/*");
        registration.setName("jwtAuthFilter");
        registration.setOrder(1);
        return registration;
    }
}
