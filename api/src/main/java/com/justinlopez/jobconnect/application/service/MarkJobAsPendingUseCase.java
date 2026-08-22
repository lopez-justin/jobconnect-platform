package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkJobAsPendingUseCase {

    private final JobRepository jobRepository;

    @Transactional
    public JobResponse execute(UUID jobId, UUID professionalId) {
        log.info("Professional {} marking job {} as pending confirmation", professionalId, jobId);

        Job job = this.jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (job.getSelectedProfessionalId() == null || !job.getSelectedProfessionalId().value().equals(professionalId)) {
            log.warn("Professional {} is not the assigned professional for job {}", professionalId, jobId);
            throw new IllegalStateException("You are not the assigned professional for this job");
        }

        if (job.getStatus() != JobStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only jobs IN_PROGRESS can be marked as pending");
        }

        job.markAsPendingConfirmation();

        Job savedJob = jobRepository.save(job);

        log.info("Job {} marked as PENDING_CONFIRMATION by professional {}", jobId, professionalId);

        return mapToJobResponse(savedJob);

    }

    private JobResponse mapToJobResponse(Job job) {
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
