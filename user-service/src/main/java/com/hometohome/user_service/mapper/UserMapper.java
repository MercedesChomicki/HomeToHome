package com.hometohome.user_service.mapper;

import com.hometohome.user_service.dto.response.UserResponseDto;
import com.hometohome.user_service.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDTO(UserEntity user);
}