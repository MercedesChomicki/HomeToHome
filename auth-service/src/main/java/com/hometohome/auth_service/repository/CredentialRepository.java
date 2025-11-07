package com.hometohome.auth_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hometohome.auth_service.model.CredentialEntity;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {
    Optional<CredentialEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}