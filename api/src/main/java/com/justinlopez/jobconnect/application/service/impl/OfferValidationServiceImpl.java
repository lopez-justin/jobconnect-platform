package com.justinlopez.jobconnect.application.service.impl;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import com.justinlopez.jobconnect.domain.service.OfferValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferValidationServiceImpl implements OfferValidationService {

    private final OfferRepository offerRepository;

    @Override
    public boolean isOfferPriceValid(Offer offer, Job job) {
        // Rule: The offer cannot be less than 30% of the budget (to avoid absurd offers)
        double minAllowed = job.getBudget().amount().doubleValue() * 0.3;
        return offer.getOfferedPrice().amount().doubleValue() >= minAllowed;
    }

    @Override
    public boolean canProfessionalOffer(UserId professionalId) {
        // Rule: A professional cannot have more than 20 pending offers at the same time
        // Future implementation: count pending offers by professional
        return true; // Placeholder for actual implementation
    }
}
