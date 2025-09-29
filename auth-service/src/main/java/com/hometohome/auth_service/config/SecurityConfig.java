package com.hometohome.auth_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.hometohome.auth_service.filters.JwtAuthFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Habilita la seguridad web
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /** Se utiliza para especificar qué patrones de URL requieren
     * seguridad y cuáles no */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/.well-known/jwks.json").permitAll()
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                // Provider que valida credenciales de login
                .authenticationProvider(authProvider())
                
                // Filtro que valida JWT para llamadas internas con SERVICE_TOKEN
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Es responsable de comprobar que las credenciales de inicio de
     * sesión sean válidas, es decir, validar al usuario.
     */
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Propósito del proveedor de autenticación:
     * - Cargar el registro de usuario de la tabla para proporcionarselo
     * al administrador de autenticacion (AuthManager)
     * - Configura como se condifica la contraseña y cómo cargar el registro del cliente
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(pwdEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    /** Codifica contraseñas. Se utiliza para cifrar la contraseña
     * y almacenar el usuario en la DB */
    @Bean
    public BCryptPasswordEncoder pwdEncoder() {
        return new BCryptPasswordEncoder();
    }
}