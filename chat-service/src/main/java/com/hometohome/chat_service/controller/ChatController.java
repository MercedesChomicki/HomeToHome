package com.hometohome.chat_service.controller;

import com.hometohome.chat_service.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        log.info("📨 Mensaje privado recibido: {} -> {}", message.getSenderId(), message.getRecipientId());

        // Enviar mensaje privado al destinatario específico
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/messages",
                message
        );
        
        log.info("✅ Mensaje privado enviado a: {}", message.getRecipientId());
    }
}