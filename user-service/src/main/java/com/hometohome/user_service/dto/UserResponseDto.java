package com.hometohome.user_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String city;
}
