package com.hometohome.pet_service.controller;

import com.hometohome.pet_service.dto.request.PetRequestDto;
import com.hometohome.pet_service.dto.response.PetResponseDto;
import com.hometohome.pet_service.service.PetServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
public class PetController {
    private final PetServiceImpl petService;

    public PetController(PetServiceImpl petService) {
        this.petService = petService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDto> getPetById(@PathVariable UUID id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @GetMapping
    public List<PetResponseDto> getAllPets() {
        return petService.getAllPets();
    }

    @PostMapping
    public ResponseEntity<PetResponseDto> createPet(@RequestBody @Valid PetRequestDto petDto) {
        PetResponseDto createdPetResponse = petService.createPet(petDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPetResponse);
    }

    @Operation
    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDto> updatePet(@PathVariable UUID id, @RequestBody @Valid PetRequestDto petDto) {
        PetResponseDto updatedPet = petService.updatePet(id, petDto);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable UUID id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}