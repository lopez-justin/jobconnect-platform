package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository {

    Job save(Job job);
    Optional<Job> findById(UUID id);
    List<Job> findByClientId(UserId clientId);
    List<Job> findPublishedJobs();

}
