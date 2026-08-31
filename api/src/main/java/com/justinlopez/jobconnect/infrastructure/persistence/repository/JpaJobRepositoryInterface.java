package com.justinlopez.jobconnect.infrastructure.persistence.repository;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaJobRepositoryInterface extends JpaRepository<JobEntity, UUID> {

    List<JobEntity> findByClientId(UUID clientId);
    List<JobEntity> findByStatus(JobStatus status);

    List<JobEntity> findAllById(Iterable<UUID> ids);

    @Query("""
        SELECT j FROM JobEntity j
        WHERE j.selectedProfessionalId = :professionalId
        ORDER BY j.createdAt DESC
    """)
    Page<JobEntity> findBySelectedProfessionalId(UUID professionalId, Pageable pageable);

    @Query("""
        SELECT j FROM JobEntity j
        WHERE (:status IS NULL OR j.status = :status)
        AND (:categoryId IS NULL OR j.category.id = :categoryId)
        AND (:city IS NULL OR j.city = :city)
        AND (:minBudget IS NULL OR j.budgetAmount >= :minBudget)
        AND (:maxBudget IS NULL OR j.budgetAmount <= :maxBudget)
        AND (:userId IS NULL OR j.client.id = :userId)
        AND (:publishedOnly = false OR j.status = 'PUBLISHED')
    """)
    Page<JobEntity> findWithFilters(
            JobStatus status,
            UUID categoryId,
            String city,
            Double minBudget,
            Double maxBudget,
            UUID userId,
            boolean publishedOnly,
            Pageable pageable
    );
}
