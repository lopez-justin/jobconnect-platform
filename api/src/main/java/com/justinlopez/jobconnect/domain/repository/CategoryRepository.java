package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findById(UUID id);

    List<Category> findAll();

}
