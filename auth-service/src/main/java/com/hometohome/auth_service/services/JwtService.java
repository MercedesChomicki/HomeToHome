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
    // @Value("${jwt.secret}")
    // private String SECRET_KEY;
    
    private final KeyPair keyPair;

    // Firmar con la clave privada
    private RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    // Para exponer desde el controller
    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    // private SecretKey getSignKey() {
    //     byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    //     return Keys.hmacShaKeyFor(keyBytes);
    // }

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
                .audience().add("user-service").and() // 👈 audience para user-service
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                //.signWith(getSignKey())
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    // Extraer desde los claims
    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
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
            .verifyWith(getPublicKey())
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
    // public Boolean validateToken(String token, UserDetails userDetails) {
    //     String email = extractEmail(token);
    //     return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    // }
}