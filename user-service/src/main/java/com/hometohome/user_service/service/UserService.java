package com.hometohome.user_service.service;

import com.hometohome.user_service.dto.UserRequestDto;
import com.hometohome.user_service.dto.UserResponseDto;
import com.hometohome.user_service.model.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    Optional<UserEntity> getUserById(UUID id);
    UserResponseDto createUser(UserRequestDto userDto);
    void deleteUser(UUID id);
}
