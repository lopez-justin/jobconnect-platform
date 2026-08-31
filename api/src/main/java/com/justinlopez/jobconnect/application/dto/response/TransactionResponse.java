package com.justinlopez.jobconnect.application.dto.response;

import com.justinlopez.jobconnect.domain.model.enums.TransactionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID jobId,
        String jobTitle,
        Double amount,
        String currency,
        TransactionStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
