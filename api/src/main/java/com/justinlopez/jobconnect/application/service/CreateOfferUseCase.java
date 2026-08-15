package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.dto.request.CreateOfferRequest;
import com.justinlopez.jobconnect.application.dto.response.OfferResponse;
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

    @Transactional
    public OfferResponse execute(CreateOfferRequest request, UUID professionalId) {
        log.info("Creating offer for jobId: {} by professionalId: {}", request.jobId(), professionalId);

        // 1. Fetch the job by ID
        var job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + request.jobId()));

        // 2. Validate business rules
        if (!this.offerValidationService.canProfessionalOffer(new UserId(professionalId))) {
            log.warn("Professional with ID: {} is not allowed to make offers", professionalId);
            throw new IllegalStateException("Professional is not allowed to make offers at this time");
        }

        // 3. Create the offer
        Money offeredPrice = new Money(BigDecimal.valueOf(request.offeredPrice()), "USD"); // Assuming USD for simplicity
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
        jobRepository.save(job);
        offerRepository.save(newOffer);

        log.info("Offer created successfully for jobId: {} by professionalId: {}", request.jobId(), professionalId);
        return mapToResponse(newOffer);

    }

    private OfferResponse mapToResponse(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getJobId(),
                offer.getProfessionalId().value(),
                offer.getOfferedPrice().amount().doubleValue(),
                offer.getOfferedPrice().currency(),
                offer.getMessage(),
                offer.getStatus(),
                offer.getCreatedAt()
        );
    }

}
