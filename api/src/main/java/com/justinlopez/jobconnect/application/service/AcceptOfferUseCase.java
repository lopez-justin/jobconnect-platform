package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobResponseAssembler;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.application.exception.ForbiddenOperationException;
import com.justinlopez.jobconnect.application.exception.ResourceNotFoundException;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.Transaction;
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
public class AcceptOfferUseCase {

    private final JobRepository jobRepository;
    private final TransactionRepository transactionRepository;
    private final JobResponseAssembler jobResponseAssembler;

    @Transactional
    public JobResponse execute(UUID jobId, UUID offerId, UUID clientId) {
        log.info("Executing AcceptOfferUseCase for clientId: {} and offerId: {}", clientId, offerId);

        // 2. Retrieve the job associated with the offer
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found for offer: " + jobId));

        // 3. Check if the job belongs to the client
        if (!job.getClientId().value().equals(clientId)) {
            log.warn("Client {} is not the owner of job {}", clientId, job.getId());
            throw new ForbiddenOperationException("You are not the owner of this job");
        }

        // 4. Delegate the acceptance of the offer to the domain model (Job.acceptOffer)
        // This changes the state of the Job to IN_PROGRESS, updates selectedProfessionalId,
        // and automatically rejects the other offers.
        job.acceptOffer(offerId);

        Offer acceptedOffer = job.getOffers().stream()
                .filter(offer -> offer.getId().equals(offerId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Accepted offer not found in aggregate"));

        Transaction transaction = new Transaction(
                null,
                job.getId(),
                job.getClientId(),
                acceptedOffer.getProfessionalId(),
                acceptedOffer.getOfferedPrice(),
                null
        );

        transaction.capture();
        this.transactionRepository.save(transaction);

        Job savedJob = this.jobRepository.save(job); // Persist the changes to the job


        log.info("Offer {} accepted. Transaction {} created with amount {}. Job status: {}",
                offerId, transaction.getId(), acceptedOffer.getOfferedPrice().amount(), savedJob.getStatus());

        return jobResponseAssembler.toResponse(savedJob);
    }

}
