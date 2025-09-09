package com.hometohome.chat_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hometohome.chat_service.client.UserServiceClient;
import com.hometohome.chat_service.dto.UserDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final UserServiceClient userServiceClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService")
    public UserDto getUser(UUID id) {
        return userServiceClient.getUserById(id);
    }
}