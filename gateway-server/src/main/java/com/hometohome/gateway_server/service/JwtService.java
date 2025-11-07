package com.hometohome.gateway_server.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.hometohome.gateway_server.model.UserPrincipal;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generar token para un usuario normal
    public String generateToken(UUID userId, String email, String name, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email); 
        claims.put("name", name);
        claims.put("role", role);
        return createToken(claims, userId, 1000 * 60 * 15);
    }

    // Generar token especial de servicio
    public String generateServiceToken(UUID serviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "SERVICE"); // 👈 claim extra
        return createToken(claims, serviceId, 1000 * 60 * 60 * 24); // 24h
    }

    private String createToken(Map<String, Object> claims, UUID userId, long jwtExpiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString()) // 👈 UUID como subject
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    // Extraer desde los claims
    public UUID extractUserId(String token) {
        String sub = extractClaim(token, Claims::getSubject);
        return UUID.fromString(sub);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("name", String.class));
    }
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validamos contra userId en lugar de email
    public Boolean validateToken(String token, UserDetails userDetails) {
        UUID userId = extractUserId(token);
        return (userId.equals(((UserPrincipal) userDetails).getId()) 
                && !isTokenExpired(token));
    }
}