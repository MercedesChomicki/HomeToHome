package com.hometohome.chat_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.hometohome.chat_service.service.ServiceTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignAuthInterceptor implements RequestInterceptor {

    private final ServiceTokenProvider serviceTokenProvider;

    @Override
    public void apply(RequestTemplate template) {
        String token = null;

        // 1) Token desde SecurityContext (user JWT) - esto cubre tanto HTTP como WebSocket
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            Object creds = auth.getCredentials();
            if(creds instanceof String s && !s.isBlank()) {
                token = s;
                log.debug("FeignAuthInterceptor: usando token desde SecurityContext para user={}", auth.getName());
            }
        }

        // 2) Si no hay token: fallback a service token SÓLO para llamadas internas autorizadas
        if (token == null || token.isBlank()) {
            if (isInternalAuthCall(template)) {
                token = serviceTokenProvider.getToken();
                log.debug("FeignAuthInterceptor: usando service token para llamada interna: {}", template.path());
            } else {
                log.debug("FeignAuthInterceptor: no se añade Authorization (no user y no es llamada interna): {}", template.path());
                return;
            }
        }

        template.header("Authorization", "Bearer " + token);        
    }

    private boolean isInternalAuthCall(RequestTemplate template) {
        String path = template.path() == null ? "" : template.path().toLowerCase();
        // Rutas "internas"
        return path.contains("/auth/") || path.contains("/internal/") || path.contains("/service-token");
    }
}