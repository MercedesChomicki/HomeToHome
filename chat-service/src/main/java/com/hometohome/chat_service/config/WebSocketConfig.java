package com.hometohome.chat_service.config;

import com.hometohome.chat_service.service.JwtService;
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
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if(accessor != null) {
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        try {
                            String token = accessor.getFirstNativeHeader("Authorization");
                            log.info("🔐 Intentando conectar WebSocket con token: {}", token != null ? "presente" : "ausente");
                            
                            if (token == null) {
                                token = accessor.getFirstNativeHeader("authorization"); // fallback
                            }
    
                            if (token != null && token.startsWith("Bearer ")) {
                                token = token.substring(7);
                                UUID userId = jwtService.extractUserId(token);
                                log.info("✅ Usuario autenticado: {}", userId);
                                
                                // Crear autenticación con el UUID
                                if(userId != null) {
                                    UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of());
                                    accessor.setUser(user);
                                    log.info("✅ Usuario seteado en CONNECT: {}", userId);
                                }
                            } else {
                                log.warn("⚠️ Token de autorización no válido o ausente");
                            }
                        } catch (Exception e) {
                            log.error("❌ Error durante la autenticación WebSocket", e);
                        }
                    }
    
                    Principal user = accessor.getUser();
                    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                        log.info("📡 SUBSCRIBE user={}, dest={}",
                            user != null ? user.getName() : "null",
                            accessor.getDestination());
                    }
    
                    if (StompCommand.SEND.equals(accessor.getCommand())) {
                        log.info("✉️  SEND user={}, dest={}",
                            user != null ? user.getName() : "null",
                            accessor.getDestination());
                    }   
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