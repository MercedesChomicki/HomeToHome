package com.hometohome.gateway_server.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private final UUID id;
    private final String email;
    private final String password;
    private final String name;
    private final String role;

    // private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        // return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // ⚠️ Spring Security usa `getUsername()` como identificador de login
    @Override
    public String getUsername() {
        return email; // usamos email para loguear
    }

    // Métodos extra de UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}