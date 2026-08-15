package com.justinlopez.jobconnect.infrastructure.web.controller;

import com.justinlopez.jobconnect.application.dto.request.CreateJobRequest;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.application.service.CreateJobUseCase;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.infrastructure.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final CreateJobUseCase createJobUseCase;
    private final JobRepository jobRepository;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal CustomUserDetailsService.UserWithId userDetails) {

        UUID clientId = userDetails.getUserId();
        JobResponse jobResponse = this.createJobUseCase.execute(request, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponse);
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getPublishedJobs() {
        List<Job> jobs = this.jobRepository.findPublishedJobs();
        List<JobResponse> response = jobs.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private JobResponse mapToResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCategory().getName(),
                job.getBudget().amount().doubleValue(),
                job.getBudget().currency(),
                job.getLocation().street(),
                job.getLocation().city(),
                job.getLocation().latitude(),
                job.getLocation().longitude(),
                job.getClientId().value(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }

}
