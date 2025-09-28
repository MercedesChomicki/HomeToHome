package com.hometohome.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hometohome.user_service.dto.request.UserRequestDto;
import com.hometohome.user_service.dto.response.UserResponseDto;
import com.hometohome.user_service.exception.ResourceNotFoundException;
import com.hometohome.user_service.mapper.UserEntityMapper;
import com.hometohome.user_service.model.UserEntity;
import com.hometohome.user_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock 
    private UserEntityMapper userEntityMapper;

    @Mock
    private BCryptPasswordEncoder pwdEncoder;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        // userEntity = new UserEntity(userId, "Alice", "alice@example.com", "1234", "NY", null, "USER");
    }

    @Test
    void givenExistingUser_whenGetUserById_thenReturnDto() {
        // Creamos un UserResponseDto simulado (lo que esperamos recibir del service)
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userId);
        dto.setName("Alice");

        // Mockeamos el repositorio: si alguien llama findById(userId), devolvemos un UserEntity existente
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        // Mockeamos el mapper: si el servicio intenta mapear el UserEntity, devolvemos nuestro dto
        when(userEntityMapper.toDTO(userEntity)).thenReturn(dto);

        // Ejecutamos el método real a testear
        UserResponseDto result = userService.getUserById(userId);

        // Verificamos que el resultado sea el esperado
        assertEquals("Alice", result.getName());
        // Confirmamos que realmente se llamó al repositorio (verificación de interacción)
        verify(userRepository).findById(userId);
    }

    @Test
    void givenNotFound_whenGetUserById_thenThrowException() {
        // Mockeamos el repositorio para que no encuentre el usuario
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Esperamos que el método lance una excepción ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void givenUsersExist_whenGetAllUsers_thenReturnDtoList() {
        // UserEntity user1 = new UserEntity(UUID.randomUUID(), "Alice", "alice@test.com", "password987!", "NY", null, "USER");
        // UserEntity user2 = new UserEntity(UUID.randomUUID(), "Bob", "bob@test.com", "password987!", "NY", null, "USER");

        // UserResponseDto dto1 = new UserResponseDto(); dto1.setId(user1.getId()); dto1.setName("Alice");
        // UserResponseDto dto2 = new UserResponseDto(); dto2.setId(user2.getId()); dto2.setName("Bob");

        // when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        // when(userEntityMapper.toDTO(user1)).thenReturn(dto1);
        // when(userEntityMapper.toDTO(user2)).thenReturn(dto2);

        // List<UserResponseDto> result = userService.getAllUsers();

        // assertEquals(2, result.size());
        // assertEquals("Alice", result.get(0).getName());
        // assertEquals("Bob", result.get(1).getName());
    }

    @Test
    void givenNoUsers_whenGetAllUsers_thenReturnEmptyList() {
        // Mock: no hay usuarios en la BD
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Ejecutamos el método
        List<UserResponseDto> result = userService.getAllUsers();

        // Validamos que el resultado sea una lista vacía
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        // Verificamos que no se intentó mapear nada
        verify(userEntityMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    void givenValidRequest_whenCreateUser_thenSaveUser() {
        // Creamos un request simulado (lo que llegaría desde el cliente)
        UserRequestDto request = buildRequestDto("Bob", "bob@example.com", "secret123", "LA");

        // Definimos lo que se va a devolver después de guardar el usuario
        UserEntity savedEntity = null; //new UserEntity(userId, "Bob", "bob@test.com", "encoded", "LA", null, "USER");
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName("Bob");

        // Mock: validamos que no existe otro usuario con ese email
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);

        // Mock: la contraseña se encripta como "encoded"
        when(pwdEncoder.encode("secret123")).thenReturn("encoded");

        // Mock: al guardar un UserEntity, devolvemos el que definimos como "guardado"
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // Mock: el mapper transforma la entidad en el DTO esperado
        when(userEntityMapper.toDTO(savedEntity)).thenReturn(responseDto);

        // Ejecutamos el método real
        UserResponseDto result = userService.createUser(request);

        // Validamos que el nombre sea el esperado.
        assertEquals("Bob", result.getName());

        // Confirmamos que efectivamente se llamó a save()
        verify(userRepository).save(argThat(user -> 
            user.getName().equals("Bob") && 
            user.getEmail().equals("bob@example.com") &&
            // user.getPassword().equals("encoded") &&
            user.getCity().equals("LA")
        ));
    }

    @Test
    void givenEmailAlreadyExists_whenCreateUser_thenThrowException() {
        // Creamos un request simulado
        UserRequestDto request = buildRequestDto("Charlie", "charlie@test.com", "password123", "Miami");

        // Mockeamos el repositorio para que diga que ese email ya existe
        when(userRepository.existsByEmail("charlie@test.com")).thenReturn(true);

        // Esperamos que el método lance la excepción IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));

        // Verificamos que nunca se intentó guardar en el repositorio (porque ya había un email registrado)
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void givenExistingUser_whenDeleteUser_thenDeleteByIdCalled() {
        // Mock: existe el usuario
        when(userRepository.existsById(userId)).thenReturn(true);

        // Ejecutamos el método
        userService.deleteUser(userId);

        // Verificamos que se llamó a deleteById
        verify(userRepository).deleteById(userId);
    }

    @Test
    void givenNonExistingUser_whenDeleteUser_thenThrowException() {
        // Mock: el usuario no existe
        when(userRepository.existsById(userId)).thenReturn(false);

        // Ejecutamos y esperamos la excepción
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));

        // Verificamos que nunca se intentó eliminar
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    private UserRequestDto buildRequestDto(String name, String email, String password, String city) {
        UserRequestDto dto = new UserRequestDto();
        dto.setName(name);
        dto.setEmail(email);
        // dto.setPassword(password);
        dto.setCity(city);
        return dto;
    }    
}