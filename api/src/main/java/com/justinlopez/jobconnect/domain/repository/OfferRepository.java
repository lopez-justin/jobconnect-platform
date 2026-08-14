package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.Offer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferRepository {
    Offer save(Offer offer);
    Optional<Offer> findById(UUID id);
    List<Offer> findByJobId(UUID jobId);
    List<Offer> findByProfessionalId(UUID professionalId);
}
