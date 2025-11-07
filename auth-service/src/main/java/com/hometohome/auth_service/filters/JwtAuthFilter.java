package com.hometohome.auth_service.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hometohome.auth_service.model.UserPrincipal;
import com.hometohome.auth_service.services.JwtService;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7);
        String role = jwtService.extractRole(jwt);

        // 👇 Solo aceptamos tokens de servicio dentro de auth-service
        if(!"SERVICE".equals(role)) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID serviceId = jwtService.extractUserId(jwt);
        UserPrincipal principal = new UserPrincipal(serviceId, "SERVICE");

        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}