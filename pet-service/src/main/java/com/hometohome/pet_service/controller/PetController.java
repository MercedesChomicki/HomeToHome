package com.hometohome.pet_service.controller;

import com.hometohome.pet_service.dto.request.PetRequestDto;
import com.hometohome.pet_service.dto.response.PetResponseDto;
import com.hometohome.pet_service.dto.response.UserDto;
import com.hometohome.pet_service.service.PetServiceImpl;
import com.hometohome.pet_service.service.UserService;
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
    private final UserService userService;

    public PetController(PetServiceImpl petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
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

    @GetMapping("/{id}/owner")
    public ResponseEntity<?> getPetOwner(@PathVariable UUID id) {
        /*UserResponseDto response = userService.getPetOwner(id);
        if (Boolean.TRUE.equals(response.getIsError())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response.getErrorMsg());
        }
        UserDto user = response.getUser();*/

        UserDto user = userService.getPetOwner(id);
        return ResponseEntity.ok("El dueño es " + user.getName() + " y vive en " + user.getCity());
    }
}