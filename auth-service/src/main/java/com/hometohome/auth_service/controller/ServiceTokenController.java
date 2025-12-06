package com.hometohome.auth_service.controller;

import com.hometohome.auth_service.config.ServiceAuthProperties;
import com.hometohome.auth_service.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ServiceTokenController {

    private final JwtService jwtService;

    private final ServiceAuthProperties serviceAuthProperties;

    private static final String SECRET_HEADER = "X-Service-Secret";
    private static final String NAME_HEADER = "X-Service-Name";

    //@PostMapping("/service-token")
    @PostMapping("/token")
    public ResponseEntity<?> issueServiceToken(
        @RequestHeader(NAME_HEADER) String serviceName,
        @RequestHeader(value = SECRET_HEADER, required = false) String secret
    ) {
        // Autenticación
        if (secret == null || !secret.equals(serviceAuthProperties.getSecrets().get(serviceName))) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        // Autorización
        List<String> scopes = serviceAuthProperties.getScopes().getOrDefault(serviceName, List.of());

        // Generar token con scopes
        String token = jwtService.generateServiceToken(serviceName, scopes);
 
        return ResponseEntity.ok(new TokenResponse(token));
    }

    public record TokenResponse(String token) {}
}