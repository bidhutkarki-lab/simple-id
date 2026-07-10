package com.example.authservice.config;

import com.example.authservice.security.AuthPrincipalArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

    public WebMvcConfig(AuthPrincipalArgumentResolver authPrincipalArgumentResolver) {
        this.authPrincipalArgumentResolver = authPrincipalArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authPrincipalArgumentResolver);
    }
}
