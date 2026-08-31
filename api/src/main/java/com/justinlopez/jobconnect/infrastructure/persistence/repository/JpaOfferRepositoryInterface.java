package com.justinlopez.jobconnect.infrastructure.persistence.repository;

import com.justinlopez.jobconnect.infrastructure.persistence.entity.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaOfferRepositoryInterface extends JpaRepository<OfferEntity, UUID> {
    List<OfferEntity> findByJobId(UUID jobId);
    List<OfferEntity> findByProfessionalId(UUID professionalId);
    @Query("""
            SELECT new com.justinlopez.jobconnect.infrastructure.persistence.repository.OfferSummaryProjection(
                o.id, o.job.id, o.professional.id, o.professional.fullName,
                o.offeredPriceAmount, o.offeredPriceCurrency, o.message, o.status, o.createdAt)
            FROM OfferEntity o
            WHERE o.job.id = :jobId
            """)
    List<OfferSummaryProjection> findOfferSummariesByJobId(@Param("jobId") UUID jobId);

    @Query("SELECT o.job.id, COUNT(o) FROM OfferEntity o WHERE o.job.id IN :jobIds GROUP BY o.job.id")
    List<Object[]> countOffersByJobIds(@Param("jobIds") List<UUID> jobIds);
}
