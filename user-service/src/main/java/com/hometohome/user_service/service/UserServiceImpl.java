package com.hometohome.user_service.service;

import com.hometohome.user_service.dto.request.UserRequestDto;
import com.hometohome.user_service.dto.response.UserResponseDto;
import com.hometohome.user_service.exception.ResourceNotFoundException;
import com.hometohome.user_service.mapper.UserEntityMapper;
import com.hometohome.user_service.model.UserEntity;
import com.hometohome.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final BCryptPasswordEncoder pwdEncoder;

    @Override
    public UserResponseDto getUserById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User",id));
        return userEntityMapper.toDTO(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userEntityMapper::toDTO)
                .toList();
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequestDto.getName());
        userEntity.setEmail(userRequestDto.getEmail());
        userEntity.setPassword(pwdEncoder.encode(userRequestDto.getPassword()));
        userEntity.setCity(userRequestDto.getCity());
        userEntity.setProfileImageUrl(userRequestDto.getProfileImageUrl());

        UserEntity savedUser = userRepository.save(userEntity);
        return userEntityMapper.toDTO(savedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: "+id, id);
        }
        userRepository.deleteById(id);
    }
}