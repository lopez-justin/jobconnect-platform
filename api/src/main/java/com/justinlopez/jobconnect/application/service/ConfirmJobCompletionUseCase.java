package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Transaction;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmJobCompletionUseCase {

    private final JobRepository jobRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public JobResponse execute(UUID jobId, UUID clientId) {
        log.info("Client {} confirming completion of job {}", clientId, jobId);

        Job job = this.jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!job.getClientId().value().equals(clientId)) {
            log.warn("Client {} is not the owner of job {}", clientId, jobId);
            throw new IllegalStateException("You are not the owner of this job");
        }

        if (job.getStatus() != JobStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Only jobs PENDING_CONFIRMATION can be confirmed");
        }

        // Confirm the job completion
        job.confirmCompletion();

        // Create a transaction for the completed job
        Transaction transaction = this.transactionRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalStateException("Transaction not found for job " + jobId));

        transaction.release();
        this.transactionRepository.save(transaction);

        log.info("Payment of {} {} released for job {}",
                transaction.getAmount().amount(),
                transaction.getAmount().currency(),
                jobId);

        Job savedJob = this.jobRepository.save(job);

        log.info("Job {} confirmed as COMPLETED by client {}", jobId, clientId);

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
