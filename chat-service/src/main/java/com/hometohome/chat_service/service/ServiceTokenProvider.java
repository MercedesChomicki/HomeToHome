package com.hometohome.chat_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hometohome.chat_service.client.AuthServiceClient;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceTokenProvider {

    private final AuthServiceClient authServiceClient;
    private final JwtService jwtService; // ya tienes JwtService que usa JwtDecoder

    @Value("${auth.service.shared-secret}")
    private String sharedSecret;

    private volatile String cachedToken;
    private volatile Instant expiry;
    private final ReentrantLock lock = new ReentrantLock();

    public String getToken() {
        // Rápido check sin lock
        if (cachedToken != null && expiry != null && Instant.now().isBefore(expiry)) {
            return cachedToken;
        }

        // lock para evitar stampede
        lock.lock();
        try {
            if (cachedToken != null && expiry != null && Instant.now().isBefore(expiry)) {
                return cachedToken;
            }

            // pedir nuevo token
            var resp = authServiceClient.getServiceToken(sharedSecret);
            String token = resp.token();

            // calcular expiración desde el JWT
            try {
                var jwt = jwtService.decode(token); // Jwt: usa JwtDecoder (añade método decode)
                var exp = jwt.getExpiresAt(); // Instant
                // setear expiry un poco antes (p.e. 10% antes) para renovación proactiva
                long secondsUntilExp = exp.getEpochSecond() - Instant.now().getEpochSecond();
                long safeSeconds = Math.max(30, (long)(secondsUntilExp * 0.9));
                this.expiry = Instant.now().plusSeconds(safeSeconds);
            } catch (Exception e) {
                // si no podemos decodificar (raro), ponemos expiry corta
                this.expiry = Instant.now().plusSeconds(60);
            }

            this.cachedToken = token;
            log.info("🔁 Nuevo service token obtenido, expira en {}", expiry);
            return cachedToken;
        } finally {
            lock.unlock();
        }
    }
}