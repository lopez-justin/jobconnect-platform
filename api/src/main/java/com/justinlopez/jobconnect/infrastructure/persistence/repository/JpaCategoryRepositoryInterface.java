package com.justinlopez.jobconnect.infrastructure.persistence.repository;

import com.justinlopez.jobconnect.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCategoryRepositoryInterface extends JpaRepository<CategoryEntity, UUID> {
}
