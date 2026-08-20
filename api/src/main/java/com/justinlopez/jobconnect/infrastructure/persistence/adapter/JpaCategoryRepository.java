package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Category;
import com.justinlopez.jobconnect.domain.repository.CategoryRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.CategoryMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaCategoryRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {

    private final JpaCategoryRepositoryInterface repository;
    private final CategoryMapper mapper;

    @Override
    public Optional<Category> findById(UUID id) {
        return this.repository.findById(id)
                .map(mapper::toDomain);
    }
}
