package com.justinlopez.jobconnect.application.dto.response;

import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OfferResponse(

        UUID id,
        UUID jobId,
        UUID professionalId,
        Double offeredPrice,
        String currency,
        String message,
        OfferStatus status,
        LocalDateTime createdAt

) {
}
