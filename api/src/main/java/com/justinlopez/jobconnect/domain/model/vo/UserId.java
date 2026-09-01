package com.justinlopez.jobconnect.domain.model.vo;

import java.util.UUID;

public record UserId(UUID value) {

    public static UserId random() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromString(String value) {
        return new UserId(UUID.fromString(value));
    }
}
