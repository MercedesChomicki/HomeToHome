package com.hometohome.user_service.client;

import java.security.interfaces.RSAPublicKey;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service", url = "${AUTH_SERVICE_URL:http://auth-service:8084}", path = "/auth")
public interface AuthFeignClient {
    @GetMapping("/public-key")
    RSAPublicKey getPublicKey();
}