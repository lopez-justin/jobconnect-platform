package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.dto.response.JobSummaryResponse;
import com.justinlopez.jobconnect.domain.model.Job;
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
public class ListMyJobsUseCase {

    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;

    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> listMyJobs(UUID professionalId, int page, int size) {
        log.info("Listing jobs assigned to professionalId: {}", professionalId);

        Pageable pageable = PageRequest.of(page, size);

        Page<Job> jobsPage = this.jobRepository.findBySelectedProfessionalId(professionalId, pageable);

        List<UUID> jobIds = jobsPage.getContent().stream()
                .map(Job::getId)
                .toList();

        Map<UUID, Integer> offersCountMap = this.offerRepository.countOffersByJobIds(jobIds);

        List<JobSummaryResponse> responses = jobsPage.getContent().stream()
                .map(job -> mapToSummary(job, offersCountMap.getOrDefault(job.getId(), 0)))
                .toList();

        return new PageImpl<>(responses, pageable, jobsPage.getTotalElements());
    }

    private JobSummaryResponse mapToSummary(Job job, int offersCount) {
        return new JobSummaryResponse(
                job.getId(),
                job.getTitle(),
                job.getCategory().getName(),
                job.getBudget().amount().doubleValue(),
                job.getBudget().currency(),
                job.getLocation().city(),
                job.getStatus(),
                offersCount,
                job.getCreatedAt()
        );
    }

}
