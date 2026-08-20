package com.justinlopez.jobconnect.application.dto.response;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record JobSummaryResponse(

        UUID id,
        String title,
        String categoryName,
        Double budgetAmount,
        String budgetCurrency,
        String city,
        JobStatus status,
        Integer offersCount,
        LocalDateTime createdAt

) {
}
