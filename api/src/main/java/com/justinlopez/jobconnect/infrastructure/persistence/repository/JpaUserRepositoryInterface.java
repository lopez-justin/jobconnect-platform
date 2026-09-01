package com.justinlopez.jobconnect.infrastructure.persistence.repository;

import com.justinlopez.jobconnect.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepositoryInterface extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
