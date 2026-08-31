package com.justinlopez.jobconnect.application.assembler;

import com.justinlopez.jobconnect.application.dto.response.JobSummaryResponse;
import com.justinlopez.jobconnect.domain.model.Job;
import org.springframework.stereotype.Component;

@Component
public class JobSummaryAssembler {

    public JobSummaryResponse toSummary(Job job, int offersCount) {
        return new JobSummaryResponse(
                job.getId(),
                job.getTitle(),
                job.getCategory().getName(),
                job.getBudget().amount().doubleValue(),
                job.getBudget().currency(),
                job.getLocation().city(),
                job.getStatus(),
                offersCount,
                job.getCreatedAt()
        );
    }
}
