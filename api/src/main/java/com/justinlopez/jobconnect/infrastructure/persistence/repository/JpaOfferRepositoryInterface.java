package com.justinlopez.jobconnect.infrastructure.persistence.repository;

import com.justinlopez.jobconnect.infrastructure.persistence.entity.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaOfferRepositoryInterface extends JpaRepository<OfferEntity, UUID> {
    List<OfferEntity> findByJobId(UUID jobId);
    List<OfferEntity> findByProfessionalId(UUID professionalId);
}
