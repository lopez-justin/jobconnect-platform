package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.OfferResponseAssembler;
import com.justinlopez.jobconnect.application.dto.response.OfferResponse;
import com.justinlopez.jobconnect.domain.model.OfferSummary;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListOffersByJobUseCase {

    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;
    private final OfferResponseAssembler offerResponseAssembler;

    @Transactional(readOnly = true)
    public List<OfferResponse> execute(UUID jobId, UUID clientId) {
        log.info("Listing offers for jobId: {} requested by clientId: {}", jobId, clientId);

        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        if (!job.getClientId().value().equals(clientId)) {
            log.warn("Client {} is not the owner of job {}", clientId, jobId);
            throw new IllegalStateException("You are not the owner of this job");
        }

        List<OfferSummary> offers = this.offerRepository.findOfferSummariesByJobId(jobId);

        return offers.stream()
                .map(offerResponseAssembler::toResponse)
                .toList();
    }

}