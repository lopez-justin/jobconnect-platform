package com.justinlopez.jobconnect.application.assembler;

import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.domain.model.Job;
import org.springframework.stereotype.Component;

@Component
public class JobResponseAssembler {

    public JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCategory().getName(),
                job.getBudget().amount().doubleValue(),
                job.getBudget().currency(),
                job.getLocation().street(),
                job.getLocation().city(),
                job.getLocation().latitude(),
                job.getLocation().longitude(),
                job.getClientId().value(),
                job.getSelectedProfessionalId() != null ? job.getSelectedProfessionalId().value() : null,
                job.getStatus(),
                job.getCreatedAt()
        );
    }
}
