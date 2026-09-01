package com.justinlopez.jobconnect.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateJobRequest(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotNull(message = "Budget amount is required")
        @Positive(message = "Budget amount must be positive")
        Double budgetAmount,

        String budgetCurrency,

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        Double latitude,
        Double longitude
) {
}
