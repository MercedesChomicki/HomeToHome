package com.hometohome.auth_service.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hometohome.auth_service.model.CredentialEntity;
import com.hometohome.auth_service.model.UserPrincipal;
import com.hometohome.auth_service.repository.CredentialRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final CredentialRepository credentialRepository;

    // Carga el registro del usuario
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CredentialEntity credential = credentialRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Credenciales no encontradas para email: " + email));
        
        return new UserPrincipal(
                credential.getId(),
                credential.getEmail(),
                credential.getPassword(),
                null, // el nombre no lo tenemos en auth-service
                credential.getRole()
                // Collections.singleton(authority)
        );
    }
}