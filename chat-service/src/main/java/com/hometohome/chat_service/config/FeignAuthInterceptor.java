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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            user = auth.getName();
            Object creds = auth.getCredentials();
            if (creds instanceof String s && !s.isBlank()) {
                token = s;
            }
            // 👇 Verificamos si el principal es un StompPrincipal
            if (auth.getPrincipal() instanceof StompPrincipal stompPrincipal) {
                token = stompPrincipal.getToken();
                user = stompPrincipal.getName();
            }
        } 

        if ((token == null || token.isBlank()) && PrincipalContextHolder.getPrincipal() != null) {
            StompPrincipal principal = PrincipalContextHolder.getPrincipal();
            token = principal.getToken();
            user = principal.getName();
        }

        // 👇 fallback al token de servicio
        if (token == null || token.isBlank()) {
            token = serviceToken;
            user = "SERVICE";
        }

        if (token != null && !token.isBlank()) {
            template.header("Authorization", "Bearer " + token);
            log.info("✅ Token agregado para usuario {} en llamada Feign", user);
        } else {
            log.warn("⚠️ No se encontró token válido ni en SecurityContextHolder ni en StompPrincipal");
        }
    }
}