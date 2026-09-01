package com.justinlopez.jobconnect.domain.service;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

public interface OfferValidationService {

    // Rule: The offer cannot be less than 50% of the budget (prevents spam)
    boolean isOfferPriceValid(Offer offer, Job job);

    // Rule: A professional cannot offer if they are deactivated or have pending jobs
    boolean canProfessionalOffer(UserId professionalId);

}
