package com.hometohome.user_service.dto.response;

import java.util.UUID;

public record LoginResponseDto (
    String token,
    String email,
    String role,
    UUID userId, 
    String name
) {}