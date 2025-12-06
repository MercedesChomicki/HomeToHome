package com.hometohome.auth_service.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hometohome.auth_service.model.UserPrincipal;
import com.hometohome.auth_service.services.JwtService;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
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
        // If it's a service token, skip here (ServiceJwtFilter handles it)
        if("SERVICE".equals(role)) {
            filterChain.doFilter(request, response);
            return;
        }

        // normal user token: subject is userId (UUID)
        try {
            UUID userId = jwtService.extractUserId(token);
            String email = jwtService.extractSubject(token);        
            UserDetails userDetails = null;
            if (email != null) {
                userDetails = userDetailsService.loadUserByUsername(email);
            } else {
                // fallback: create minimal UserPrincipal with id (no credentials)
                userDetails = new UserPrincipal(userId, "USER");
            }

            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
            );
        
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            // invalid token or user not found -> continue without auth
        }

        filterChain.doFilter(request, response);
    }
}