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
    private final JwtService jwtService; // usa JwtDecoder

   
    @Value("${auth.service.shared-secret:${AUTH_SERVICE_SHARED_SECRET:super-secret-local}}")
    private String sharedSecret;

    private volatile String cachedToken;
    private volatile Instant expiry;
    private final ReentrantLock lock = new ReentrantLock();

    public String getToken() {
        // Fast path sin lock
        if (cachedToken != null && expiry != null && Instant.now().isBefore(expiry)) {
            return cachedToken;
        }

        // lock para evitar stampede
        lock.lock();
        try {
            if (cachedToken != null && expiry != null && Instant.now().isBefore(expiry)) {
                return cachedToken;
            }

            log.debug("🔄 Service-token vencido o inexistente, solicitando uno nuevo al Auth-Service…");

            // Pedir nuevo token al Auth-Service
            var resp = authServiceClient.getServiceToken(sharedSecret);
            String token = resp.token();

            // Calcular expiración desde el JWT
            try {
                var jwt = jwtService.decode(token); // Jwt: usa JwtDecoder
                var exp = jwt.getExpiresAt(); // Instant

                if(exp != null) {
                    long secondsUntilExp = exp.getEpochSecond() - Instant.now().getEpochSecond();
                    secondsUntilExp = Math.max(secondsUntilExp, 30); // evitar números negativos

                    // Renovar antes del 10% final
                    long safeSeconds = Math.max(30, (long)(secondsUntilExp * 0.9));
                    this.expiry = Instant.now().plusSeconds(safeSeconds);
                } else {
                    // Si el token viene sin expiración (raro), ponemos 1 minuto
                    this.expiry = Instant.now().plusSeconds(60);
                }

            } catch (Exception e) {
                log.warn("⚠️ No se pudo decodificar el service-token para obtener expiración. Usando fallback de 60s.");
                this.expiry = Instant.now().plusSeconds(60);
            }

            this.cachedToken = token;
            log.info("✅ Nuevo service-token obtenido. Renovación programada antes de: {}", this.expiry);
            log.debug("🔐 (trace) Nuevo token: {}", token);

            return cachedToken;
        } finally {
            lock.unlock();
        }
    }

    /** Fuerza renovar (útil en tests) */
    public void invalidate() {
        lock.lock();
        try {
            cachedToken = null;
            expiry = null;
        } finally {
            lock.unlock();
        }
    }
}