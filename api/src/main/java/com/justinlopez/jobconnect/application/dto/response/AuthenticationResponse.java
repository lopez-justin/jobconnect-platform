package com.justinlopez.jobconnect.application.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AuthenticationResponse(

        UUID userId,
        String email,
        String fullName,
        List<String> roles,
        String accessToken,
        String refreshToken,
        String tokenType
) { }
