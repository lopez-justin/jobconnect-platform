package com.justinlopez.jobconnect.application.service.impl;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import com.justinlopez.jobconnect.domain.service.OfferValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferValidationServiceImpl implements OfferValidationService {

    private static final int MAX_PENDING_OFFERS = 20;

    private final OfferRepository offerRepository;

    @Override
    public boolean isOfferPriceValid(Offer offer, Job job) {
        // Rule: The offer cannot be less than 30% of the budget (to avoid absurd offers)
        double minAllowed = job.getBudget().amount().doubleValue() * 0.3;
        return offer.getOfferedPrice().amount().doubleValue() >= minAllowed;
    }

    @Override
    public boolean canProfessionalOffer(UserId professionalId) {
        // Rule: A professional cannot have more than MAX_PENDING_OFFERS pending offers at the same time
        long pendingOffers = offerRepository.findByProfessionalId(professionalId.value()).stream()
                .filter(offer -> offer.getStatus() == OfferStatus.PENDING)
                .count();
        return pendingOffers < MAX_PENDING_OFFERS;
    }
}
