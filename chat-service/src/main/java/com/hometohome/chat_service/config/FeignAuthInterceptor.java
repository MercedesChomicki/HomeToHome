package com.hometohome.chat_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Value("${service.token}")
    private String serviceToken;

    @Override
    public void apply(RequestTemplate template) {

        String token = null;
        String user = null;

        // 1️⃣ Token desde WebSocket (StompPrincipal)
        var principal = PrincipalContextHolder.getPrincipal();
        if (principal != null && principal.getToken() != null) {
            token = principal.getToken();
            user = principal.getName();
            log.info("🧩 Feign → token desde StompPrincipal (user={})", user);
        }

        // 2️⃣ Token desde SecurityContextHolder (HTTP)
        if (token == null || token.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() instanceof String creds && !creds.isBlank()) {
                token = creds;
                user = auth.getName();
                log.info("🔐 Feign → token desde SecurityContextHolder (user={})", user);
            }
        }

        // 3️⃣ Fallback SOLO si la llamada es interna autorizada
        if (token == null || token.isBlank()) {
            if (isInternalAuthCall(template)) {
                token = serviceToken;
                user = "SERVICE";
                log.warn("⚠️ Feign → usando SERVICE TOKEN (llamada interna a AuthService)");
            } else {
                log.warn("⛔ Feign → NO se enviará token (sin usuario y no es llamada interna)");
                return; // No agregamos header
            }
        }

        template.header("Authorization", "Bearer " + token);
    }

    /**
     * Determina si esta llamada Feign es “interna permitida”
     * para usar el service-token.
     */
    private boolean isInternalAuthCall(RequestTemplate template) {
        String url = template.path().toLowerCase();

        // ✅ Ajustar rutas internas permitidas
        return url.contains("/internal/") || url.contains("/auth/validate");
    }
}