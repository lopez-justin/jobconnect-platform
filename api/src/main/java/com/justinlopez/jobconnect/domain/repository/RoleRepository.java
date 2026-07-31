package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.Role;
import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByName(UserRoleName name);

}
