package com.hometohome.pet_service.dto.request;

import com.hometohome.pet_service.model.enums.Size;
import com.hometohome.pet_service.model.enums.Species;
import com.hometohome.pet_service.model.validator.ValidEnum;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PetRequestDto {
    @NotBlank
    private String name;
    @NotNull
    private LocalDate birthDate;
    @ValidEnum(enumClass = Species.class, message = "Invalid species value")
    private String species;
    @ValidEnum(enumClass = Size.class, message = "Invalid size value")
    private String size;
    @NotBlank
    private String story;
    private List<String> photosUrl;
    @NotNull
    private UUID ownerId;
}

