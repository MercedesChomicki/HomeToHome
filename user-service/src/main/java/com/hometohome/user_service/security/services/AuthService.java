package com.hometohome.user_service.security.services;

import com.hometohome.user_service.dto.request.LoginRequestDto;
import com.hometohome.user_service.dto.request.UserRequestDto;
import com.hometohome.user_service.dto.response.LoginResponseDto;
import com.hometohome.user_service.dto.response.UserResponseDto;
import com.hometohome.user_service.mapper.UserEntityMapper;
import com.hometohome.user_service.model.UserEntity;
import com.hometohome.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder pwdEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserEntityMapper userEntityMapper;

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) throw new UsernameNotFoundException("Usuario no encontrado");

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        authManager.authenticate(token);

        String jwt = jwtService.generateToken(loginRequest.getEmail());
        return new LoginResponseDto(jwt, user.getEmail(), user.getRole());
    }

    public UserResponseDto registerUser(UserRequestDto dto) {
        log.info("Attempting to register user with email: {}", dto.getEmail());
        
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("User with email {} already exists", dto.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya está registrado");
        }

        try {
            UserEntity user = new UserEntity();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(pwdEncoder.encode(dto.getPassword()));
            user.setCity(dto.getCity());
            // Set profileImageUrl only if it's not null
            if (dto.getProfileImageUrl() != null && !dto.getProfileImageUrl().trim().isEmpty()) {
                user.setProfileImageUrl(dto.getProfileImageUrl());
            }

            log.info("Saving user to database: {}", user.getEmail());
            UserEntity savedUser = userRepository.save(user);
            log.info("User saved successfully with ID: {}", savedUser.getId());
            
            return userEntityMapper.toDTO(savedUser);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar usuario: " + e.getMessage());
        }
    }
}
