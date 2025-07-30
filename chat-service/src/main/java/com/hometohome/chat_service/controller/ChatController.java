package com.hometohome.chat_service.controller;

import com.hometohome.chat_service.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());

        String channelId = generateChannelId(message.getSenderId(), message.getRecipientId());

        // Envía a una ruta personalizada basada en el recipientId
        messagingTemplate.convertAndSend(
                "/topic/private-chat/" + channelId,
                message
        );
    }

    private String generateChannelId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "-" + user2 : user2 + "-" + user1;
    }
}