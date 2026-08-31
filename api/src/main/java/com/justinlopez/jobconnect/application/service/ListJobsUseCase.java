package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobSummaryAssembler;
import com.justinlopez.jobconnect.application.dto.request.JobListRequest;
import com.justinlopez.jobconnect.application.dto.response.JobSummaryResponse;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListJobsUseCase {

    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;
    private final JobSummaryAssembler jobSummaryAssembler;

    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> listJobs(JobListRequest request, UserId userId, String role) {
        log.info("Listing jobs with filters: {}, user: {}, role: {}", request, userId, role);

        Pageable pageable = PageRequest.of(request.page(), request.size());

        Page<Job> jobsPage = jobRepository.findByFilters(
                request.status(),
                request.category(),
                request.city(),
                request.minBudget(),
                request.maxBudget(),
                userId,
                role,
                pageable
        );

        List<UUID> jobIds = jobsPage.getContent().stream()
                .map(Job::getId)
                .toList();

        Map<UUID, Integer> offersCountMap = this.offerRepository.countOffersByJobIds(jobIds);

        List<JobSummaryResponse> responses = jobsPage.getContent().stream()
                .map(job -> jobSummaryAssembler.toSummary(job, offersCountMap.getOrDefault(job.getId(), 0)))
                .toList();

        return new PageImpl<>(responses, pageable, jobsPage.getTotalElements());

    }

}
