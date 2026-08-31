package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobResponseAssembler;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.application.exception.ConflictException;
import com.justinlopez.jobconnect.application.exception.ForbiddenOperationException;
import com.justinlopez.jobconnect.application.exception.ResourceNotFoundException;
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
    private final JobResponseAssembler jobResponseAssembler;

    @Transactional
    public JobResponse execute(UUID jobId, UUID professionalId) {
        log.info("Professional {} marking job {} as pending confirmation", professionalId, jobId);

        Job job = this.jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getSelectedProfessionalId() == null || !job.getSelectedProfessionalId().value().equals(professionalId)) {
            log.warn("Professional {} is not the assigned professional for job {}", professionalId, jobId);
            throw new ForbiddenOperationException("You are not the assigned professional for this job");
        }

        if (job.getStatus() != JobStatus.IN_PROGRESS) {
            throw new ConflictException("Only jobs IN_PROGRESS can be marked as pending");
        }

        job.markAsPendingConfirmation();

        Job savedJob = jobRepository.save(job);

        log.info("Job {} marked as PENDING_CONFIRMATION by professional {}", jobId, professionalId);

        return jobResponseAssembler.toResponse(savedJob);

    }

}
