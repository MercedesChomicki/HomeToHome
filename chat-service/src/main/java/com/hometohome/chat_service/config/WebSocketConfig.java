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

                if (accessor == null) {
                    return message;
                }
    
                StompCommand command = accessor.getCommand();
    
                // ✅ CONNECT: validar token, setear principal y authentication
                if (StompCommand.CONNECT.equals(command)) {
                    String headerAuth = accessor.getFirstNativeHeader("Authorization");
                    if (headerAuth == null) {
                        headerAuth = accessor.getFirstNativeHeader("authorization");
                    }
    
                    if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                        try {
                            String token = headerAuth.substring(7);
                            Jwt jwt = jwtDecoder.decode(token); // <-- Usaremos el jwtDecoder de Spring
    
                            String userId = jwt.getSubject(); // sub = userId
                            log.info("✅ WebSocket CONNECT auth userId={}", userId);
    
                            StompPrincipal principal = new StompPrincipal(userId, token);
                            accessor.setUser(principal);
    
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userId, token, List.of());
    
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } catch (Exception e) {
                            log.error("❌ Error al autenticar WebSocket token", e);
                        }
                    } else {
                        log.warn("⚠️ CONNECT sin token");
                    }
                }

                // ✅ SEND: Si el principal existe → guardarlo para Feign
                if (StompCommand.SEND.equals(command) && accessor.getUser() instanceof StompPrincipal principal) {
                    log.info("✉️ SEND from user={}", principal.getName());
                    PrincipalContextHolder.setPrincipal(principal);
                }

                // (Opcional) logging de SUBSCRIBE
                if (StompCommand.SUBSCRIBE.equals(command)) {
                    log.info("📡 SUBSCRIBE user={}, dest={}",
                            accessor.getUser() != null ? accessor.getUser().getName() : "null",
                            accessor.getDestination());
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
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // 👈 no /user/queue
        registry.setApplicationDestinationPrefixes("/app"); // destinos que van al @MessageMapping
        registry.setUserDestinationPrefix("/user"); // Prefijo para mensajes privados
    }
}