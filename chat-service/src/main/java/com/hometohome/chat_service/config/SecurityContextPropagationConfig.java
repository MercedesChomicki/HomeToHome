package com.hometohome.chat_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class SecurityContextPropagationConfig {

    @PostConstruct
    public void init() {
        // 🔑 Esto permite que el SecurityContext se herede en los hilos hijos
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}