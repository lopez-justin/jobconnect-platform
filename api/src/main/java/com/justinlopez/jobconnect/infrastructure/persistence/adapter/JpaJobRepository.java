package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

        List<OfferEntity> offerEntities = job.getOffers().stream()
                .map(offer -> {
                    OfferEntity offerEntity = offerMapper.toEntity(offer);
                    offerEntity.setJob(entity);
                    return offerEntity;
                })
                .toList();
        entity.setOffers(offerEntities);

        JobEntity savedEntity = this.repository.save(entity);

        return findById(savedEntity.getId())
                .orElseThrow(() -> new IllegalStateException("Failed to reload saved job"));
    }

    @Override
    public Optional<Job> findById(UUID id) {
        return this.repository.findById(id)
                .map(entity -> toDomainWithOffers(entity, jpaOfferRepository.findByJobId(id)));
    }

    @Override
    public List<Job> findByIds(java.util.Collection<UUID> ids) {
        List<JobEntity> entities = this.repository.findAllById(ids);
        return toDomainWithOffers(entities);
    }

    @Override
    public List<Job> findByClientId(UserId clientId) {
        return toDomainWithOffers(this.repository.findByClientId(clientId.value()));
    }

    @Override
    public List<Job> findPublishedJobs() {
        return toDomainWithOffers(this.repository.findByStatus(JobStatus.PUBLISHED));
    }

    @Override
    public Page<Job> findBySelectedProfessionalId(UUID professionalId, Pageable pageable) {
        Page<JobEntity> page = this.repository.findBySelectedProfessionalId(professionalId, pageable);
        return page.map(entity -> toDomainWithOffers(entity, jpaOfferRepository.findByJobId(entity.getId())));
    }

    @Override
    public Page<Job> findByFilters(JobStatus status, UUID categoryId, String city, Double minBudget, Double maxBudget, UserId userId, UserRoleName role, Pageable pageable) {
        boolean publishedOnly = role == UserRoleName.PROFESSIONAL;
        UUID clientId = role == UserRoleName.CLIENT && userId != null ? userId.value() : null;

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

        return jobEntities.map(entity -> toDomainWithOffers(entity, jpaOfferRepository.findByJobId(entity.getId())));
    }

    /**
     * Maps a list of job entities to domain aggregates, loading ALL offers for the
     * given jobs in a single batched query (avoids the N+1 select problem).
     */
    private List<Job> toDomainWithOffers(List<JobEntity> entities) {
        if (entities.isEmpty()) {
            return List.of();
        }
        List<UUID> jobIds = entities.stream().map(JobEntity::getId).toList();
        Map<UUID, List<OfferEntity>> offersByJob = jpaOfferRepository.findByJobIdIn(jobIds).stream()
                .collect(Collectors.groupingBy(offer -> offer.getJob().getId()));

        return entities.stream()
                .map(entity -> toDomainWithOffers(
                        entity,
                        offersByJob.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    private Job toDomainWithOffers(JobEntity entity, List<OfferEntity> offerEntities) {
        Job job = mapper.toDomain(entity);
        List<Offer> offers = offerEntities.stream()
                .map(offerMapper::toDomain)
                .toList();
        job.initializeOffers(offers);
        return job;
    }
}
