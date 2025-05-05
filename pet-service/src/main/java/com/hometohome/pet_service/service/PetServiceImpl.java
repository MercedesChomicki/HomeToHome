package com.hometohome.pet_service.service;

import com.hometohome.pet_service.dto.request.PetRequestDto;
import com.hometohome.pet_service.dto.response.PetResponseDto;
import com.hometohome.pet_service.exception.ResourceNotFoundException;
import com.hometohome.pet_service.mapper.PetMapper;
import com.hometohome.pet_service.model.entity.Pet;
import com.hometohome.pet_service.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PetServiceImpl implements PetService {
    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final UserService userService;

    public PetServiceImpl(PetRepository petRepository, PetMapper petMapper, UserService userService) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
        this.userService = userService;
    }

    @Override
    public PetResponseDto getPetById(UUID id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet",id));
        return petMapper.toDTO(pet);
    }

    @Override
    public List<PetResponseDto> getAllPets() {
        return petRepository.findAll()
                .stream()
                .map(petMapper::toDTO)
                .toList();
    }

    @Override
    public PetResponseDto createPet(PetRequestDto petRequestDto) {
        userService.getUser(petRequestDto.getOwnerId()); // valida existencia
        Pet pet = petMapper.toEntity(petRequestDto);
        Pet savedPet = petRepository.save(pet);
        return petMapper.toDTO(savedPet);
    }

    @Override
    public PetResponseDto updatePet(UUID id, PetRequestDto petDto) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet",id));
        petMapper.updatePetFromDto(petDto, existingPet);
        Pet savedPet = petRepository.save(existingPet);
        return petMapper.toDTO(savedPet);
    }

    @Override
    public void deletePet(UUID id) {
        if(!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet", id);
        }
        petRepository.deleteById(id);
    }
}