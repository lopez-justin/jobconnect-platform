package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OfferSummary(
        UUID id,
        UUID jobId,
        UUID professionalId,
        String professionalFullName,
        BigDecimal offeredPrice,
        String currency,
        String message,
        OfferStatus status,
        OffsetDateTime createdAt
) {
}