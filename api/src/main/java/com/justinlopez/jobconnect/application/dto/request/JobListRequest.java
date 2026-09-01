package com.justinlopez.jobconnect.application.dto.request;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;

import java.util.UUID;

public record JobListRequest(

        JobStatus status,
        UUID category,
        String city,
        Double minBudget,
        Double maxBudget,
        Integer page,
        Integer size

) {

    public JobListRequest {
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 20;
        }
    }

}
