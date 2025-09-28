package com.hometohome.auth_service.controller;

import com.hometohome.auth_service.services.JwtService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwkSetEndpoint {

    private final JwtService jwtService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        RSAKey key = new RSAKey.Builder(jwtService.getPublicKey())
                .keyID("auth-service-key") // un id para identificar la key
                .build();
        return new JWKSet(key).toJSONObject();
    }
}