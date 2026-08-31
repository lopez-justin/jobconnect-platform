package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobResponseAssembler;
import com.justinlopez.jobconnect.application.dto.request.CreateJobRequest;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.domain.model.Category;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.vo.Address;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.CategoryRepository;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.service.JobValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateJobUseCase {

    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;
    private final JobValidationService jobValidationService;
    private final JobResponseAssembler jobResponseAssembler;

    @Transactional
    public JobResponse execute(CreateJobRequest request, UUID clienteId) {

        log.info("Creating job with title: {} for clientId: {}", request.title(), clienteId);
        log.info("Request details: {}", request);

        // 1. Validate business rules to see if the client can create a job
        if (!jobValidationService.canClientCreateJob(new UserId(clienteId))) {
            log.warn("Client with id {} is not allowed to create a job", clienteId);
            throw new IllegalStateException("Client is not allowed to create a job");
        }

        // 2. Validate that the category exists
        Category category = this.categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.categoryId()));

        // 3. Build value objects and domain model for the new job
        String currency = request.budgetCurrency() != null ? request.budgetCurrency() : "USD";
        Money budget = new Money(BigDecimal.valueOf(request.budgetAmount()), currency);

        BigDecimal latitude = request.latitude() != null ? BigDecimal.valueOf(request.latitude()) : BigDecimal.ZERO;
        BigDecimal longitude = request.longitude() != null ? BigDecimal.valueOf(request.longitude()) : BigDecimal.ZERO;
        Address location = new Address(request.street(), request.city(), latitude, longitude);

        Job newJob = new Job(
                null,
                request.title(),
                request.description(),
                category,
                budget,
                location,
                new UserId(clienteId)
        );

        // 4. Persist the new job
        Job savedJob = this.jobRepository.save(newJob);
        log.info("Job created successfully with id: {}", savedJob.getId());

        return jobResponseAssembler.toResponse(savedJob);
    }

}
