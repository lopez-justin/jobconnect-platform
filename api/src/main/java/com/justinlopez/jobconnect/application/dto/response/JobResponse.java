package com.justinlopez.jobconnect.application.dto.response;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record JobResponse(
        UUID id,
        String title,
        String description,
        String categoryName,
        Double budgetAmount,
        String budgetCurrency,
        String street,
        String city,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID clientId,
        UUID selectedProfessionalId,
        JobStatus status,
        OffsetDateTime createdAt
) {
}
