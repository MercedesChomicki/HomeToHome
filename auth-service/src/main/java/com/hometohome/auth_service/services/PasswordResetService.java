package com.hometohome.auth_service.services;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hometohome.auth_service.model.CredentialEntity;
import com.hometohome.auth_service.model.PasswordResetToken;
import com.hometohome.auth_service.repository.CredentialRepository;
import com.hometohome.auth_service.repository.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final CredentialRepository credentialRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder pwdEncoder;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void sendResetToken(String email) {
        CredentialEntity user = credentialRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no encontrado"));

        // Generar token aleatorio o JWT
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)); // 15 minutos
        tokenRepo.save(resetToken);

        log.info("Generated reset token for {}", email);

        // Enviar correo con el link:
        emailService.send(email, "Recuperación de contraseña",
            "Haz clic aquí: " + frontendUrl + "/reset-password?token=" + token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido"));

        if (resetToken.isUsed() || resetToken.getExpiration().before(new Date())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado o ya usado");
        }

        CredentialEntity user = credentialRepo.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setPassword(pwdEncoder.encode(newPassword));
        credentialRepo.save(user);

        resetToken.setUsed(true);
        tokenRepo.save(resetToken);

        log.info("Password reset for {}", user.getEmail());
    }
}
