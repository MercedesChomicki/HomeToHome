package com.hometohome.auth_service.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa una identidad "service" (otro microservicio).
 * Authorities: ROLE_SERVICE + SCOPE_xxx (prefijo SCOPE_)
 */
@Getter
public class ServicePrincipal implements UserDetails{
    private final String serviceName;
    private final List<String> scopes;

    public ServicePrincipal(String serviceName, List<String> scopes) {
        this.serviceName = serviceName;
        this.scopes = scopes != null ? scopes : Collections.emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority("ROLE_SERVICE"));
        auth.addAll(scopes.stream()
                .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
                .collect(Collectors.toList()));
        return auth;
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return serviceName; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}