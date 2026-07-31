package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;

import java.util.UUID;

public class Role {

    private final UUID id;
    private final UserRoleName name;
    private final String description;

    public Role(UUID id, UserRoleName name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public UserRoleName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }
}
