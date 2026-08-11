package com.justinlopez.jobconnect.application.dto.request;

import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Email
        @Size(min = 1, max = 200)
        String email,

        @NotBlank
        @Size(min = 1, max = 150)
        String fullName,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @NotBlank
        @Size(min = 6, max = 100)
        String confirmPassword,

        @Size(min = 10, max = 15)
        String phone,

        @NotNull
        UserRoleName role

) {
}
