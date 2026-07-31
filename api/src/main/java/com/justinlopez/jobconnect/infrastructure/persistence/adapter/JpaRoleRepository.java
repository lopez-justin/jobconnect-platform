package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Role;
import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;
import com.justinlopez.jobconnect.domain.repository.RoleRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.RoleMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaRoleRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaRoleRepository implements RoleRepository {

    private final JpaRoleRepositoryInterface repository;
    private final RoleMapper mapper;

    @Override
    public Optional<Role> findByName(UserRoleName name) {
        return this.repository
                .findByName(name)
                .map(mapper::toDomain);
    }
}
