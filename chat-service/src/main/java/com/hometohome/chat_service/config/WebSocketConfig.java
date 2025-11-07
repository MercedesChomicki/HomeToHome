package com.hometohome.chat_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtDecoder jwtDecoder;

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) return message;
    
                StompCommand command = accessor.getCommand();
    
                // ✅ CONNECT: validar token, setear principal y authentication
                if (StompCommand.CONNECT.equals(command)) {
                    String header = accessor.getFirstNativeHeader("Authorization");
                    if (header == null) header = accessor.getFirstNativeHeader("authorization");
    
                    if (header != null && header.startsWith("Bearer ")) {
                        try {
                            String token = header.substring(7);
                            Jwt jwt = jwtDecoder.decode(token); // <-- Usaremos el jwtDecoder de Spring
                            String userId = jwt.getSubject(); // sub = userId
                            log.info("✅ WebSocket CONNECT auth userId={}", userId);
    
                            String role = jwt.getClaims().getOrDefault("role", "USER").toString();
                            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
    
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userId, token, authorities);
    
                            // set SecurityContext and as Principal for STOMP
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.setUser(authentication);

                            log.info("WebSocket CONNECT authenticated userId={} role={}", userId, role);
                        } catch (Exception e) {
                            log.warn("WebSocket CONNECT token inválido: {}", e.getMessage());
                        }
                    } else {
                        log.warn("WebSocket CONNECT sin Authorization header");
                    }
                }

                // NOTE: For SEND/SUBSCRIBE we can log user; SecurityContext already populated on CONNECT
                if (StompCommand.SEND.equals(command)) {
                    var user = accessor.getUser();
                    log.debug("STOMP SEND user={}, dest={}", user != null ? user.getName() : "null", accessor.getDestination());
                }

                if (StompCommand.SUBSCRIBE.equals(command)) {
                    var user = accessor.getUser();
                    log.debug("STOMP SUBSCRIBE user={}, dest={}", user != null ? user.getName() : "null", accessor.getDestination());
                }

                return message;
            }
        });
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Configurar endpoints para funcionar a través del gateway
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173");
                //.withSockJS(); // RECOMENDADO en ambientes reales por fallback, pero opcional
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // 👈 no /user/queue
        registry.setApplicationDestinationPrefixes("/app"); // destinos que van al @MessageMapping
        registry.setUserDestinationPrefix("/user"); // Prefijo para mensajes privados
    }
}