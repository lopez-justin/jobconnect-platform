package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.OfferSummary;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.OfferEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.OfferMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaOfferRepositoryInterface;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.OfferSummaryProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public List<OfferSummary> findOfferSummariesByJobId(UUID jobId) {
        return this.repository.findOfferSummariesByJobId(jobId).stream()
                .map(JpaOfferRepository::toSummary)
                .toList();
    }

    private static OfferSummary toSummary(OfferSummaryProjection projection) {
        return new OfferSummary(
                projection.id(),
                projection.jobId(),
                projection.professionalId(),
                projection.professionalFullName(),
                projection.offeredPrice(),
                projection.currency(),
                projection.message(),
                projection.status(),
                OffsetDateTimeToLocalDateTime(projection.createdAt())
        );
    }

    private static LocalDateTime OffsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

    @Override
    public Map<UUID, Integer> countOffersByJobIds(List<UUID> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) return Map.of();

        List<Object[]> results = this.repository.countOffersByJobIds(jobIds);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (UUID) result[0],
                        result -> ((Long) result[1]).intValue()
                ));
    }
}
