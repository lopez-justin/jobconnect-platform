package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.vo.Email;

import java.util.Set;
import java.util.UUID;

public class User {

    private final UUID id;
    private final Email email;
    private final String fullName;
    private final String passwordHash;
    private final String phone;
    private final boolean active;
    private final Set<Role> roles;

    public User(UUID id, Email email, String fullName, String passwordHash, String phone, boolean active, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.active = active;
        this.roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

}
