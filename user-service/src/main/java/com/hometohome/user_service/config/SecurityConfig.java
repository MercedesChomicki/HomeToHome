package com.hometohome.user_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity // Habilita la seguridad web
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/users").permitAll() // registro
                .requestMatchers(HttpMethod.GET, "/users/by-email").hasRole("SERVICE") // 👈 acepta tokens con rol SERVICE
                .anyRequest().authenticated()
            )
            //.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // valida JWT
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> 
                jwt.jwtAuthenticationConverter(jwtAuthConverter())))
            .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            String aud = jwt.getAudience().isEmpty() ? null : jwt.getAudience().get(0);
            log.info("Token role claim: {}, audience: {}", role, aud);

            if ("user-service".equals(aud) && role != null) {
                return List.of(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return List.of();
        });

        return converter;
    }
}