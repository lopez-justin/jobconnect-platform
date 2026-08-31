package com.justinlopez.jobconnect.application.service.impl;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.service.JobValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobValidationServiceImpl implements JobValidationService {

    private static final int MAX_ACTIVE_JOBS = 5;

    private static final Set<JobStatus> ACTIVE_STATUSES = EnumSet.of(
            JobStatus.PUBLISHED,
            JobStatus.IN_PROGRESS,
            JobStatus.PENDING_CONFIRMATION
    );

    private final JobRepository jobRepository;

    @Override
    public boolean canClientCreateJob(UserId clientId) {
        long activeJobs = jobRepository.findByClientId(clientId).stream()
                .map(Job::getStatus)
                .filter(ACTIVE_STATUSES::contains)
                .count();
        return activeJobs < MAX_ACTIVE_JOBS;
    }

}
