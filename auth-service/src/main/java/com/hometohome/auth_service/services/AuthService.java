package com.hometohome.auth_service.services;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hometohome.auth_service.clients.UserClient;
import com.hometohome.auth_service.dto.AuthRequest;
import com.hometohome.auth_service.dto.AuthResponse;
import com.hometohome.auth_service.dto.RegisterRequest;
import com.hometohome.auth_service.dto.UserRequestDto;
import com.hometohome.auth_service.dto.UserResponseDto;
import com.hometohome.auth_service.model.CredentialEntity;
import com.hometohome.auth_service.repository.CredentialRepository;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder pwdEncoder;
    private final JwtService jwtService;
    private final UserClient userClient;

    public AuthResponse register(RegisterRequest request) {
        // Validar que no exista el email        
        if (credentialRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }

        // Guardar credenciales (auth-service DB)
        CredentialEntity cred = new CredentialEntity();
        cred.setEmail(request.getEmail());
        cred.setPassword(pwdEncoder.encode(request.getPassword()));
        cred.setRole("USER");
        credentialRepository.save(cred);

        // Llamar a user-service para crear perfil
        UserRequestDto userReq = new UserRequestDto();
        userReq.setCredentialId(cred.getId());
        userReq.setEmail(request.getEmail());
        userReq.setName(request.getName());
        userReq.setCity(request.getCity());
        userReq.setImage(request.getImage());

        // 🚀 Feign se encarga de la llamada REST (sin HttpHeaders)
        UserResponseDto profile = userClient.createUser(userReq);

        // Generar token JWT (userId = profile.id)
        String token = jwtService.generateToken(
            profile.getId(), 
            profile.getEmail(), 
            profile.getName(), 
            "USER"
        );

        return new AuthResponse(token, profile);
    }

    public AuthResponse login(AuthRequest request) {
        CredentialEntity cred = credentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!pwdEncoder.matches(request.getPassword(), cred.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        // obtener perfil (opcional)
        UserResponseDto profile = userClient.findByEmail(request.getEmail());

        String token = jwtService.generateToken(
            profile.getId(), 
            profile.getEmail(), 
            profile.getName(), 
            cred.getRole()
        );
        
        return new AuthResponse(token, profile);
    }

    @Bean
    public RequestInterceptor authInterceptor(JwtService jwtService) {
        return requestTemplate -> {
            String serviceToken = jwtService.generateServiceToken(UUID.randomUUID());
            requestTemplate.header("Authorization", "Bearer " + serviceToken);
        };
    }
}