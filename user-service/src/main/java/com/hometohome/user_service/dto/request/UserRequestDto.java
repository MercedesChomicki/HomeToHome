package com.hometohome.user_service.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class UserRequestDto {
    private UUID credentialId;
    private String email;
    private String name;
    private String city;
    private String image;
}