package com.hometohome.user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    // @Column(nullable = false, unique = true)
    @Column(unique = true)
    private UUID credentialId; // FK al auth-service

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(name = "image")
    private String image;
}