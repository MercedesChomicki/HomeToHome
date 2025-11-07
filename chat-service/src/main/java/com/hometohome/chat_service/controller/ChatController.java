package com.hometohome.chat_service.controller;

import com.hometohome.chat_service.dto.ChatMessageDto;
import com.hometohome.chat_service.model.ChatMessage;
import com.hometohome.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message, Principal principal) {

        if (principal == null) {
            log.warn("⛔ Mensaje rechazado: Principal es null (no autenticado)");
            return;
        }

        try {
            UUID senderId = UUID.fromString(principal.getName());
            message.setTimestamp(LocalDateTime.now());
            message.setSenderId(senderId);

            log.info("📨 Mensaje recibido: {} ➡ {}", senderId, message.getRecipientId());

            // Obtener nombre del usuario vía Feign
            String senderUsername;
            try {
                senderUsername = chatService.getUser(senderId).getName();
            } catch (Exception ex) {
                senderUsername = "Unknown";
                log.error("❗ No se pudo obtener el nombre del usuario {} via UserService: {}", senderId, ex.getMessage());
            }

            ChatMessageDto dto = new ChatMessageDto(
                    message.getSenderId(),
                    senderUsername,
                    message.getRecipientId(),
                    message.getContent(),
                    message.getTimestamp()
            );

            // Enviar mensaje privado al destinatario específico
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId().toString(),
                    "/queue/messages",
                    dto
            );

            log.info("✅ Enviado a user={} → /queue/messages: {}", message.getRecipientId(), dto);

        } finally {
            // cleanup SecurityContext for this thread to avoid leaking auth to other tasks
            SecurityContextHolder.clearContext();
        }
    }
}