package com.hometohome.user_service.dto;

import lombok.Getter;

@Getter
public class UserRequestDto {
    private String name;
    private String email;
    private String password;
    private String city;
    private String profileImageUrl;
}
