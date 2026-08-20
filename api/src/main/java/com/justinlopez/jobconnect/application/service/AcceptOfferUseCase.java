package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcceptOfferUseCase {

    private final JobRepository jobRepository;

    @Transactional
    public JobResponse execute(UUID jobId, UUID offerId, UUID clientId) {
        log.info("Executing AcceptOfferUseCase for clientId: {} and offerId: {}", clientId, offerId);

        // 2. Retrieve the job associated with the offer
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found for offer: " + jobId));

        // 3. Check if the job belongs to the client
        if (!job.getClientId().value().equals(clientId)) {
            log.warn("Client {} is not the owner of job {}", clientId, job.getId());
            throw new IllegalStateException("You are not the owner of this job");
        }

        // 4. Delegate the acceptance of the offer to the domain model (Job.acceptOffer)
        // This changes the state of the Job to IN_PROGRESS, updates selectedProfessionalId,
        // and automatically rejects the other offers.
        job.acceptOffer(offerId);

        Job savedJob = this.jobRepository.save(job); // Persist the changes to the job


        log.info("Offer {} accepted successfully for job {}", offerId, jobId);

        return new JobResponse(
                savedJob.getId(),
                savedJob.getTitle(),
                savedJob.getDescription(),
                savedJob.getCategory().getName(),
                savedJob.getBudget().amount().doubleValue(),
                savedJob.getBudget().currency(),
                savedJob.getLocation().street(),
                savedJob.getLocation().city(),
                savedJob.getLocation().latitude(),
                savedJob.getLocation().longitude(),
                savedJob.getClientId().value(),
                savedJob.getSelectedProfessionalId() != null ? savedJob.getSelectedProfessionalId().value() : null,
                savedJob.getStatus(),
                savedJob.getCreatedAt()
        );
    }

}
