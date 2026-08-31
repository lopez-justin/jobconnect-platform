package com.justinlopez.jobconnect.application.dto.response;

import com.justinlopez.jobconnect.domain.model.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID jobId,
        String jobTitle,
        Double amount,
        String currency,
        TransactionStatus status,
        String stripePaymentIntentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
