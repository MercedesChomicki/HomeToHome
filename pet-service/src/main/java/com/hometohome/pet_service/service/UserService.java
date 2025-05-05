package com.hometohome.pet_service.service;

import com.hometohome.pet_service.client.UserServiceClient;
import com.hometohome.pet_service.dto.response.UserDto;
import com.hometohome.pet_service.exception.UserNotFoundException;
import com.hometohome.pet_service.exception.ResourceNotFoundException;
import com.hometohome.pet_service.exception.ServiceUnavailableException;
import com.hometohome.pet_service.model.entity.Pet;
import com.hometohome.pet_service.repository.PetRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserService {
    private final UserServiceClient userServiceClient;
    private final PetRepository petRepository;

    public UserService(UserServiceClient userServiceClient, PetRepository petRepository) {
        this.userServiceClient = userServiceClient;
        this.petRepository = petRepository;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService")
    public UserDto getUser(UUID id) {
        return userServiceClient.getUserById(id);
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getPetOwnerFallback")
    @Retry(name = "userService")
    public UserDto getPetOwner(UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Pet",id));
        return getUser(pet.getOwnerId());
    }

    public UserDto getUserFallback(UUID id, Throwable t) {
        if (t instanceof UserNotFoundException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, t.getMessage());
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de usuarios no está disponible.");
    }

    public UserDto getPetOwnerFallback(UUID petId, Throwable t) {
        throw new ServiceUnavailableException("No se pudo obtener el dueño. Intente más tarde.");
    }
}
