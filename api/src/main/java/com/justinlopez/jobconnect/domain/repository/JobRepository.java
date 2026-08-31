package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository {

    Job save(Job job);
    Optional<Job> findById(UUID id);
    List<Job> findByIds(Collection<UUID> ids);
    List<Job> findByClientId(UserId clientId);
    List<Job> findPublishedJobs();

    Page<Job> findBySelectedProfessionalId(UUID professionalId, Pageable pageable);

    Page<Job> findByFilters(
            JobStatus status,
            UUID categoryId,
            String city,
            Double minBudget,
            Double maxBudget,
            UserId userId,
            UserRoleName role,
            Pageable pageable
    );
}
