package com.hometohome.auth_service.controller;

import com.hometohome.auth_service.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ServiceTokenController {

    private final JwtService jwtService;

    @Value("${auth.service.secret}")
    private String serviceSecret;

    // Header simple para autenticar la petición entre servicios
    private static final String HEADER = "X-Service-Secret";

    @PostMapping("/service-token")
    public ResponseEntity<?> issueServiceToken(@RequestHeader(value = HEADER, required = false) String secret) {
        if (secret == null || !secret.equals(serviceSecret)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        // Usamos un serviceId fijo o configurable
        UUID serviceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String token = jwtService.generateServiceToken(serviceId);

        return ResponseEntity.ok(new TokenResponse(token));
    }

    public record TokenResponse(String token) {}
}