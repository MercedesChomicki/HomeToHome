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

import com.hometohome.auth_service.model.ServicePrincipal;
import com.hometohome.auth_service.services.JwtService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceJwtAuthFilter extends OncePerRequestFilter {
    
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
        
        String token = authHeader.substring(7);

        String role;
        try {
            role = jwtService.extractRole(token);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!"SERVICE".equals(role)) {
            // not a service token — let other filters handle
            filterChain.doFilter(request, response);
            return;
        }

        // valid service token: build ServicePrincipal with scopes -> authorities
        String serviceName = jwtService.extractSubject(token);
        List<String> scopes = jwtService.extractScopes(token);
        
        ServicePrincipal principal = new ServicePrincipal(serviceName, scopes);

        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(
                principal, 
                null, 
                principal.getAuthorities()
            );
        
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}