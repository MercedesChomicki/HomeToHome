package com.hometohome.user_service.security.controller;

import com.hometohome.user_service.dto.request.LoginRequestDto;
import com.hometohome.user_service.dto.request.UserRequestDto;
import com.hometohome.user_service.dto.response.LoginResponseDto;
import com.hometohome.user_service.dto.response.UserResponseDto;
import com.hometohome.user_service.security.services.AuthService;
import com.hometohome.user_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User service is running!");
    }

    @GetMapping("/db-test")
    public ResponseEntity<String> dbTest() {
        try {
            long count = userRepository.count();
            return ResponseEntity.ok("Database connection OK. User count: " + count);
        } catch (Exception e) {
            log.error("Database test failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database test failed: " + e.getMessage());
        }
    }

    @PostMapping("/test-register")
    public ResponseEntity<String> testRegister(@RequestBody UserRequestDto dto) {
        log.info("TEST REGISTRO - Received data: name={}, email={}, city={}, profileImageUrl={}", 
                dto.getName(), dto.getEmail(), dto.getCity(), dto.getProfileImageUrl());
        return ResponseEntity.ok("Test registration received successfully");
    }

    @PostMapping("/register-no-validation")
    public ResponseEntity<UserResponseDto> registerNoValidation(@RequestBody UserRequestDto dto) {
        try {
            log.info("REGISTER NO VALIDATION - Received data: name={}, email={}, city={}, profileImageUrl={}", 
                    dto.getName(), dto.getEmail(), dto.getCity(), dto.getProfileImageUrl());
            
            UserResponseDto newUser = authService.registerUser(dto);
            log.info("NUEVO USUARIO: {}", newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            log.error("Error during registration (no validation): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRequestDto dto) {
        try {
            log.info("REGISTRO LLAMADO - Received data: name={}, email={}, city={}, profileImageUrl={}", 
                    dto.getName(), dto.getEmail(), dto.getCity(), dto.getProfileImageUrl());
            
            UserResponseDto newUser = authService.registerUser(dto);
            log.info("NUEVO USUARIO: {}", newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage(), e);
            throw e;
        }
    }
}