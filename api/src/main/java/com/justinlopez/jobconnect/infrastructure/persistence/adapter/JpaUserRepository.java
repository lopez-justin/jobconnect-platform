package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.User;
import com.justinlopez.jobconnect.domain.repository.UserRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.UserMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaUserRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final JpaUserRepositoryInterface repository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return this.repository
                .findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.repository
                .findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return this.repository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return this.repository
                .existsByPhone(phone);
    }
}
