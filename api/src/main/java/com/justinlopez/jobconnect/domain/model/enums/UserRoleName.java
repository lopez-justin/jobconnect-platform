package com.justinlopez.jobconnect.domain.model.enums;

public enum UserRoleName {
    CLIENT,
    PROFESSIONAL,
    ADMIN;

    public static UserRoleName fromString(String name) {
        try {
            return UserRoleName.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid role name: " + name);
        }
    }
}
