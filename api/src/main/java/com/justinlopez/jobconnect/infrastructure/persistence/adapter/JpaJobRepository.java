package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.JobEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.JobMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaJobRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaJobRepository implements JobRepository {

    private final JpaJobRepositoryInterface repository;
    private final JobMapper mapper;

    @Override
    public Job save(Job job) {
        JobEntity entity = this.mapper.toEntity(job);
        JobEntity savedEntity = this.repository.save(entity);
        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Job> findById(UUID id) {
        return this.repository
                .findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Job> findByClientId(UserId clientId) {
        return this.repository
                .findByClientId(clientId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Job> findPublishedJobs() {
        return this.repository.findByStatus(JobStatus.PUBLISHED)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
