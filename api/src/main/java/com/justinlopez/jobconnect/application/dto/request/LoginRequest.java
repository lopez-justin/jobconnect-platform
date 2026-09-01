package com.justinlopez.jobconnect.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank()
        @Email()
        @Size(min = 1, max = 200)
        String email,

        @NotBlank()
        @Size(min = 6, max = 100)
        String password

) {
}
