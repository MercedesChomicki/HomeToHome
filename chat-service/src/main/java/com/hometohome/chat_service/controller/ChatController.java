package com.hometohome.chat_service.controller;

import com.hometohome.chat_service.dto.ChatMessageDto;
import com.hometohome.chat_service.model.ChatMessage;
import com.hometohome.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
            log.warn("⚠️ Principal es null en sendMessage, no autenticado");
            return;
        }
        
        message.setTimestamp(LocalDateTime.now());
        message.setSenderId(UUID.fromString(principal.getName())); // UUID del JWT
        
        log.info("📨 Mensaje privado recibido: {} -> {}", message.getSenderId(), message.getRecipientId());

        String senderUsername = chatService.getUser(message.getSenderId()).getName();

        ChatMessageDto dto = new ChatMessageDto(
            message.getSenderId(),
            senderUsername,
            message.getRecipientId(),
            message.getContent(),
            message.getTimestamp()
        );

        // Enviar mensaje privado al destinatario específico
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId().toString(), // UUID del receptor
                "/queue/messages",
                dto
        );

        log.info("➡️ Enviando a usuario={} destino=/queue/messages: {}", message.getRecipientId(), dto);
    }
}