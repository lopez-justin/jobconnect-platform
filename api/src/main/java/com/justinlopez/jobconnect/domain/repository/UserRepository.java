package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);

}
