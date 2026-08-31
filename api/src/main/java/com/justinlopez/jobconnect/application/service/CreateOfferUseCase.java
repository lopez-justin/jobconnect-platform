package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.OfferResponseAssembler;
import com.justinlopez.jobconnect.application.dto.request.CreateOfferRequest;
import com.justinlopez.jobconnect.application.dto.response.OfferResponse;
import com.justinlopez.jobconnect.application.exception.ForbiddenOperationException;
import com.justinlopez.jobconnect.application.exception.ResourceNotFoundException;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import com.justinlopez.jobconnect.domain.service.OfferValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOfferUseCase {

    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;
    private final OfferValidationService offerValidationService;
    private final OfferResponseAssembler offerResponseAssembler;

    @Transactional
    public OfferResponse execute(CreateOfferRequest request, UUID professionalId) {
        log.info("Creating offer for jobId: {} by professionalId: {}", request.jobId(), professionalId);

        // 1. Fetch the job by ID
        var job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + request.jobId()));

        // 2. Validate business rules
        if (!this.offerValidationService.canProfessionalOffer(new UserId(professionalId))) {
            log.warn("Professional with ID: {} is not allowed to make offers", professionalId);
            throw new ForbiddenOperationException("Professional is not allowed to make offers at this time");
        }

        // 3. Create the offer using the job's budget currency (no fixed currency)
        Money offeredPrice = new Money(BigDecimal.valueOf(request.offeredPrice()), job.getBudget().currency());
        Offer newOffer = new Offer(
                null,
                job.getId(),
                new UserId(professionalId),
                offeredPrice,
                request.message()
        );

        // 4. Delegate the business logic to the domain model
        job.addOffer(newOffer);

        // 5. Persist the job
        Job savedJob = jobRepository.save(job);

        Offer savedOffer = savedJob.getOffers().stream()
                .filter(offer -> offer.getProfessionalId().value().equals(professionalId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Saved offer not found in job aggregate"));

        log.info("Offer created successfully for jobId: {} by professionalId: {}", request.jobId(), professionalId);
        return offerResponseAssembler.toResponse(savedOffer);

    }

}
