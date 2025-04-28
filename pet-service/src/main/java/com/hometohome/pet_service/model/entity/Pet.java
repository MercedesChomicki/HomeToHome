package com.hometohome.pet_service.model.entity;

import com.hometohome.pet_service.model.enums.Size;
import com.hometohome.pet_service.model.enums.Species;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * UUID ownerId:
 * En arquitectura de microservicios, no se referencia directamente a entidades de otros servicios.
 * En este caso debo almacenar el ID del usuario y cuando necesite datos del usuario (nombre, email, etc.),
 * debo hacer una llamada REST (o gRPC) desde pet-service a user-service para obtener la información.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pets")
public class Pet {
    @Id
    @Setter(AccessLevel.NONE)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species species;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Size size;

    @Column(nullable = false)
    private String story;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> photosUrl;

    @Column(name = "adopted", nullable = false)
    private Boolean isAdopted = false;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "require_application", nullable = false)
    private Boolean isRequireApplication = false;
}
