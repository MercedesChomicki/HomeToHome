package com.hometohome.pet_service.mapper;

import com.hometohome.pet_service.dto.request.PetRequestDto;
import com.hometohome.pet_service.dto.response.PetResponseDto;
import com.hometohome.pet_service.model.entity.Pet;
import com.hometohome.pet_service.util.DateUtils;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PetMapper {
    @Mapping(source = "birthDate", target = "formattedAge", qualifiedByName = "birthDateToFormattedAge")
    PetResponseDto toDTO(Pet pet);

    @Named("birthDateToFormattedAge")
    default String birthDateToFormattedAge(java.time.LocalDate birthDate) {
        return DateUtils.formatAge(birthDate);
    }

    Pet toEntity(PetRequestDto petRequestDto);

    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePetFromDto(PetRequestDto dto, @MappingTarget Pet entity);
}