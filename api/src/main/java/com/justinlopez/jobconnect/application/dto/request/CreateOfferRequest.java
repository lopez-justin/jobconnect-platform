package com.justinlopez.jobconnect.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateOfferRequest(

        @NotNull(message = "Job ID is required")
        UUID jobId,

        @NotNull(message = "Offered price is required")
        @Positive(message = "Offered price must be a positive number")
        Double offeredPrice,

        String message
) {
}
