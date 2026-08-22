package com.justinlopez.jobconnect.infrastructure.web.controller;

import com.justinlopez.jobconnect.application.dto.request.CreateJobRequest;
import com.justinlopez.jobconnect.application.dto.request.JobListRequest;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.application.dto.response.JobSummaryResponse;
import com.justinlopez.jobconnect.application.service.AcceptOfferUseCase;
import com.justinlopez.jobconnect.application.service.CreateJobUseCase;
import com.justinlopez.jobconnect.application.service.ListJobsUseCase;
import com.justinlopez.jobconnect.application.service.MarkJobAsPendingUseCase;
import com.justinlopez.jobconnect.application.service.ConfirmJobCompletionUseCase;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.infrastructure.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final CreateJobUseCase createJobUseCase;
    private final AcceptOfferUseCase acceptOfferUseCase;
    private final ListJobsUseCase listJobsUseCase;
    private final MarkJobAsPendingUseCase markJobAsPendingUseCase;
    private final ConfirmJobCompletionUseCase confirmJobCompletionUseCase;
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

    /*@GetMapping
    public ResponseEntity<List<JobResponse>> getPublishedJobs() {
        List<Job> jobs = this.jobRepository.findPublishedJobs();
        List<JobResponse> response = jobs.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }*/

    @PostMapping("/{jobId}/offers/{offerId}/accept")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<JobResponse> acceptOffer(
            @PathVariable UUID jobId,
            @PathVariable UUID offerId,
            @AuthenticationPrincipal CustomUserDetailsService.UserWithId userDetails) {

        UUID clientId = userDetails.getUserId();
        JobResponse response = this.acceptOfferUseCase.execute(jobId, offerId, clientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<JobSummaryResponse>> listJobs(
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetailsService.UserWithId userDetails
    ) {

        JobListRequest request = new JobListRequest(
                status,
                categoryId,
                city,
                minBudget,
                maxBudget,
                page,
                size
        );

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_CLIENT")
                .replace("ROLE_", "");

        Page<JobSummaryResponse> jobsPage = this.listJobsUseCase.listJobs(
                request,
                new UserId(userDetails.getUserId()),
                role
        );

        return ResponseEntity.ok(jobsPage);
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
                job.getSelectedProfessionalId() != null ? job.getSelectedProfessionalId().value() : null,
                job.getStatus(),
                job.getCreatedAt()
        );
    }

    @PostMapping("/{jobId}/mark-pending")
    @PreAuthorize("hasRole('PROFESSIONAL')")
    public ResponseEntity<JobResponse> markJobAsPending(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal CustomUserDetailsService.UserWithId userDetails
    ) {
        JobResponse response = this.markJobAsPendingUseCase.execute(jobId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{jobId}/confirm-completion")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<JobResponse> confirmCompletion(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal CustomUserDetailsService.UserWithId userDetails
    ) {
        JobResponse response = confirmJobCompletionUseCase.execute(jobId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
