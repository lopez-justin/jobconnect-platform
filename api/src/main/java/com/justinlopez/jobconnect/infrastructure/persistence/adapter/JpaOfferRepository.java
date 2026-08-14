package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.OfferEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.OfferMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaOfferRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaOfferRepository implements OfferRepository {

    private final JpaOfferRepositoryInterface repository;
    private final OfferMapper mapper;

    @Override
    public Offer save(Offer offer) {
        OfferEntity entity = this.mapper.toEntity(offer);
        OfferEntity savedEntity = this.repository.save(entity);
        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Offer> findById(UUID id) {
        return this.repository.findById(id).map(this.mapper::toDomain);
    }

    @Override
    public List<Offer> findByJobId(UUID jobId) {
        return this.repository.findByJobId(jobId).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public List<Offer> findByProfessionalId(UUID professionalId) {
        return this.repository.findByProfessionalId(professionalId).stream()
                .map(this.mapper::toDomain)
                .toList();
    }
}
