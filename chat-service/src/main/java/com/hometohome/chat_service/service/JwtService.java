package com.hometohome.chat_service.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtDecoder jwtDecoder;

    public UUID extractUserId(String token) throws JwtException {
        Jwt jwt = decode(token);
        // sub fue emitido como UUID en tu auth-service
        return UUID.fromString(jwt.getSubject());
    }

    public String extractClaimAsString(String token, String claimName) throws JwtException {
        Jwt jwt = decode(token);
        Object claim = jwt.getClaims().get(claimName);
        return claim != null ? claim.toString() : null;
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwt jwt = decode(token);
            Instant expInstant = jwt.getExpiresAt();
            Date exp = expInstant != null ? Date.from(expInstant) : null;
            return exp != null && exp.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public Jwt decode(String token) throws JwtException {
        // JwtDecoder espera el token "sin Bearer "
        return jwtDecoder.decode(token);
    }
}