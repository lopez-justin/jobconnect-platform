package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobResponseAssembler;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.application.exception.ConflictException;
import com.justinlopez.jobconnect.application.exception.ForbiddenOperationException;
import com.justinlopez.jobconnect.application.exception.ResourceNotFoundException;
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
    private final JobResponseAssembler jobResponseAssembler;

    @Transactional
    public JobResponse execute(UUID jobId, UUID clientId) {
        log.info("Client {} confirming completion of job {}", clientId, jobId);

        Job job = this.jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getClientId().value().equals(clientId)) {
            log.warn("Client {} is not the owner of job {}", clientId, jobId);
            throw new ForbiddenOperationException("You are not the owner of this job");
        }

        if (job.getStatus() != JobStatus.PENDING_CONFIRMATION) {
            throw new ConflictException("Only jobs PENDING_CONFIRMATION can be confirmed");
        }

        // Confirm the job completion
        job.confirmCompletion();

        // Create a transaction for the completed job
        Transaction transaction = this.transactionRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for job " + jobId));

        transaction.release();
        this.transactionRepository.save(transaction);

        log.info("Payment of {} {} released for job {}",
                transaction.getAmount().amount(),
                transaction.getAmount().currency(),
                jobId);

        Job savedJob = this.jobRepository.save(job);

        log.info("Job {} confirmed as COMPLETED by client {}", jobId, clientId);

        return jobResponseAssembler.toResponse(savedJob);
    }

}
