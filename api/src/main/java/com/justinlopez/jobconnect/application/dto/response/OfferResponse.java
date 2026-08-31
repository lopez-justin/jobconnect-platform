package com.justinlopez.jobconnect.application.dto.response;

import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OfferResponse(

        UUID id,
        UUID jobId,
        UUID professionalId,
        String professionalFullName,
        Double offeredPrice,
        String currency,
        String message,
        OfferStatus status,
        OffsetDateTime createdAt

) {
}
