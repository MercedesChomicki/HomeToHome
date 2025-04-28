package com.hometohome.pet_service.service;

import com.hometohome.pet_service.dto.request.PetRequestDto;
import com.hometohome.pet_service.dto.response.PetResponseDto;

import java.util.List;
import java.util.UUID;

public interface PetService {
    List<PetResponseDto> getAllPets();
    PetResponseDto getPetById(UUID id);
    PetResponseDto createPet(PetRequestDto petDto);
    PetResponseDto updatePet(UUID id, PetRequestDto petDto);
    void deletePet(UUID id);
}
