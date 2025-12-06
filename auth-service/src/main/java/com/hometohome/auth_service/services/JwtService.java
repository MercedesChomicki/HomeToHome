package com.hometohome.auth_service.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.hometohome.auth_service.model.UserPrincipal;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {
    
    private final KeyPair keyPair;

    // Firmar con la clave privada
    private RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    // Para exponer desde el controller
    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    // User token (subject = UUID userId)
    public String generateUserToken(UUID userId, String email, String name, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email); 
        claims.put("name", name);
        claims.put("role", role);
        return createToken(claims, userId.toString(), 1000 * 60 * 15);
    }

    // Service token (subject = serviceName OR serviceId)
    public String generateServiceToken(String serviceName, List<String> scopes) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "SERVICE");
        claims.put("serviceName", serviceName);
        claims.put("scopes", scopes != null ? scopes : List.of());
        return createToken(claims, serviceName, 1000 * 60 * 60 * 24); // 24h
    }

    private String createToken(Map<String, Object> claims, String subject, long jwtExpiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject) // Nombre del servicio o userId
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    // Extractors
    public UUID extractUserId(String token) {
        String sub = extractClaim(token, Claims::getSubject);
        return UUID.fromString(sub);
    }
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("name", String.class));
    }
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    @SuppressWarnings("unchecked")
    public List<String> extractScopes(String token) {
        return extractClaim(token, claims -> (List<String>) claims.getOrDefault("scopes", List.of()));
    }
    public String extractServiceName(String token) {
        return extractClaim(token, claims -> claims.get("serviceName", String.class));
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getPublicKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        UUID userId = extractUserId(token);
        return (userId.equals(((UserPrincipal) userDetails).getId()) 
                && !isTokenExpired(token));
    }
}