package com.hometohome.pet_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PetResponseDto {
    private UUID id;
    private String name;
    private String formattedAge;
    private String species;
    private String size;
    private String story;
    private List<String> photosUrl;
    private Boolean adopted;
    private UUID ownerId;
    private Boolean requireApplication;
}
