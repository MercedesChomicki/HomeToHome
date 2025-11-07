package com.hometohome.chat_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @PostMapping("/auth/service-token")
    TokenResponse getServiceToken(@RequestHeader("X-Service-Secret") String secret);

    record TokenResponse(String token) {}
}