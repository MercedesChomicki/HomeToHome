package com.hometohome.gateway_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
        ServerHttpSecurity http, 
        ReactiveJwtDecoder jwtDecoder
    ) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(auth -> auth
                .pathMatchers("/api/auth/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwt -> jwt.jwtDecoder(jwtDecoder)
            ))
            .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // Usa la misma secret key que auth-service
        return NimbusReactiveJwtDecoder.withSecretKey(
            new javax.crypto.spec.SecretKeySpec(
                java.util.Base64.getDecoder().decode(jwtSecret),
                "HmacSHA256"
            )
        ).build();
    }
} 