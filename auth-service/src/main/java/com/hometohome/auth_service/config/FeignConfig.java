package com.hometohome.auth_service.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hometohome.auth_service.services.JwtService;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final JwtService jwtService;

    @Bean
    public RequestInterceptor authInterceptor() {
        return requestTemplate -> {
            // Token de servicio con rol SERVICE
            String serviceToken = jwtService.generateToken(
                UUID.fromString("11111111-1111-1111-1111-111111111111"), // serviceId fijo
                "auth-service@internal", // email dummy
                "Auth Service",          // nombre dummy
                "SERVICE"                // rol clave!
            );
            log.debug("Injecting service token into Feign call");
            requestTemplate.header("Authorization", "Bearer " + serviceToken);
        };
    }
}