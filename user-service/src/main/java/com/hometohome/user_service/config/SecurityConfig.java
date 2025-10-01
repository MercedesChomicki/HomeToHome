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
                // 1️⃣ Endpoints públicos (sin autenticación)
                .requestMatchers(HttpMethod.POST, "/users").permitAll() // registro
                // 2️⃣ Endpoints exclusivos para comunicación interna (entre microservicios)
                .requestMatchers(HttpMethod.GET, "/users/by-email").hasRole("SERVICE") // 👈 acepta tokens con rol SERVICE
                // 3️⃣ Endpoints accesibles para usuarios logueados o servicios internos
                .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("USER", "ADMIN", "SERVICE")
                // 4️⃣ Cualquier otra request requiere autenticación
                .anyRequest().authenticated()
            )
            // Configuración de JWT como recurso protegido
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> 
                jwt.jwtAuthenticationConverter(jwtAuthConverter())))
            .build();
    }

    /**
     * Convierte el claim "role" del JWT en una autoridad de Spring Security.
     * Ejemplo: role=USER → ROLE_USER
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            // 🔑 Todos los usuarios logueados tienen un rol
            if (role != null) {
                return List.of(new SimpleGrantedAuthority("ROLE_" + role));
            }
            // 🔒 En caso de que sea un token raro, no se asigna nada
            return List.of();
        });

        return converter;
    }
}