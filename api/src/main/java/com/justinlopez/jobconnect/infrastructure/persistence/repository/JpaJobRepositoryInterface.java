package com.justinlopez.jobconnect.infrastructure.persistence.repository;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaJobRepositoryInterface extends JpaRepository<JobEntity, UUID> {

    List<JobEntity> findByClientId(UUID clientId);
    List<JobEntity> findByStatus(JobStatus status);

}
