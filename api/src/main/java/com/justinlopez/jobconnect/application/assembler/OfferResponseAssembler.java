package com.justinlopez.jobconnect.application.assembler;

import com.justinlopez.jobconnect.application.dto.response.OfferResponse;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.OfferSummary;
import org.springframework.stereotype.Component;

@Component
public class OfferResponseAssembler {

    public OfferResponse toResponse(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getJobId(),
                offer.getProfessionalId().value(),
                null,
                offer.getOfferedPrice().amount().doubleValue(),
                offer.getOfferedPrice().currency(),
                offer.getMessage(),
                offer.getStatus(),
                offer.getCreatedAt()
        );
    }

    public OfferResponse toResponse(OfferSummary offer) {
        return new OfferResponse(
                offer.id(),
                offer.jobId(),
                offer.professionalId(),
                offer.professionalFullName(),
                offer.offeredPrice() != null ? offer.offeredPrice().doubleValue() : null,
                offer.currency(),
                offer.message(),
                offer.status(),
                offer.createdAt()
        );
    }
}
