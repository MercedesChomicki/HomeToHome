package com.hometohome.user_service.service;

import com.hometohome.user_service.dto.request.UserRequestDto;
import com.hometohome.user_service.dto.response.UserResponseDto;
import com.hometohome.user_service.exception.ResourceNotFoundException;
import com.hometohome.user_service.mapper.UserEntityMapper;
import com.hometohome.user_service.model.UserEntity;
import com.hometohome.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    // private final BCryptPasswordEncoder pwdEncoder;

    public UserResponseDto getUserById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User",id));
        return userEntityMapper.toDTO(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userEntityMapper::toDTO)
                .toList();
    }

    public UserResponseDto getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado", email));
        return userEntityMapper.toDTO(user);
    }

    public UserResponseDto createUser(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setCredentialId(request.getCredentialId());
        userEntity.setEmail(request.getEmail());
        userEntity.setName(request.getName());
        // userEntity.setPassword(pwdEncoder.encode(request.getPassword()));
        userEntity.setCity(request.getCity());
        userEntity.setImage(request.getImage());

        UserEntity savedUser = userRepository.save(userEntity);
        return userEntityMapper.toDTO(savedUser);
    }

    public void deleteUser(UUID id) {
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: "+id, id);
        }
        userRepository.deleteById(id);
    }
}