package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.JobEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.OfferEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.JobMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.OfferMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaJobRepositoryInterface;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaOfferRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaJobRepository implements JobRepository {

    private final JpaJobRepositoryInterface repository;
    private final JpaOfferRepositoryInterface jpaOfferRepository;
    private final JobMapper mapper;
    private final OfferMapper offerMapper;

    @Override
    public Job save(Job job) {
        JobEntity entity = this.mapper.toEntity(job);

        job.getOffers().stream()
                .filter(offer -> offer.getStatus() == OfferStatus.ACCEPTED)
                .findFirst()
                .ifPresent(acceptedOffer -> entity.setSelectedOfferId(acceptedOffer.getId()));

        JobEntity savedEntity = this.repository.save(entity);

        List<OfferEntity> offerEntities = job.getOffers().stream()
                .map(offer -> {
                    OfferEntity offerEntity = offerMapper.toEntity(offer);
                    offerEntity.setJob(savedEntity);
                    return offerEntity;
                })
                .toList();

        jpaOfferRepository.saveAll(offerEntities);

        return findById(savedEntity.getId())
                .orElseThrow(() -> new IllegalStateException("Failed to reload saved job"));
    }

    @Override
    public Optional<Job> findById(UUID id) {
        return this.repository.findById(id)
                .map(jobEntity -> {
                    // 1. Mapear el Job raíz
                    Job job = mapper.toDomain(jobEntity);

                    // 2. Cargar TODAS las ofertas asociadas a este trabajo
                    List<Offer> offers = jpaOfferRepository.findByJobId(id).stream()
                            .map(offerMapper::toDomain)
                            .toList();

                    // 3. Inicializar la lista de ofertas en el agregado
                    job.initializeOffers(offers);

                    return job;
                });
    }

    @Override
    public List<Job> findByIds(java.util.Collection<UUID> ids) {
        return this.repository.findAllById(ids).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public List<Job> findByClientId(UserId clientId) {
        return this.repository
                .findByClientId(clientId.value())
                .stream()
                .map(entity -> findById(entity.getId()).orElseThrow())
                .toList();
    }

    @Override
    public List<Job> findPublishedJobs() {
        return this.repository.findByStatus(JobStatus.PUBLISHED)
                .stream()
                .map(entity -> findById(entity.getId()).orElseThrow())
                .toList();
    }

    @Override
    public Page<Job> findBySelectedProfessionalId(UUID professionalId, Pageable pageable) {
        return this.repository.findBySelectedProfessionalId(professionalId, pageable)
                .map(this.mapper::toDomain);
    }

    @Override
    public Page<Job> findByFilters(JobStatus status, UUID categoryId, String city, Double minBudget, Double maxBudget, UserId userId, String role, Pageable pageable) {
        boolean publishedOnly = "PROFESSIONAL".equalsIgnoreCase(role);
        UUID clientId = "CLIENT".equalsIgnoreCase(role) && userId != null ? userId.value() : null;

        Page<JobEntity> jobEntities = this.repository.findWithFilters(
                status,
                categoryId,
                city,
                minBudget,
                maxBudget,
                clientId,
                publishedOnly,
                pageable
        );

        return jobEntities.map(this.mapper::toDomain);

    }
}
