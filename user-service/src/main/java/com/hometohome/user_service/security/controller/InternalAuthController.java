package com.hometohome.user_service.security.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hometohome.user_service.security.services.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {
    private final JwtService jwtService;

    @GetMapping("/service-token")
    public String getServiceToken() {
        return jwtService.generateServiceToken(
            UUID.fromString("11111111-1111-1111-1111-111111111111")
        );
    }
}