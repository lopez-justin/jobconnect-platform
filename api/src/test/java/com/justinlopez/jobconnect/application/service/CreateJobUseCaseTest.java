package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobResponseAssembler;
import com.justinlopez.jobconnect.application.dto.request.CreateJobRequest;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.application.exception.ForbiddenOperationException;
import com.justinlopez.jobconnect.application.exception.ResourceNotFoundException;
import com.justinlopez.jobconnect.domain.model.Category;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.repository.CategoryRepository;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.service.JobValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateJobUseCaseTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private JobValidationService jobValidationService;
    @Mock
    private JobResponseAssembler jobResponseAssembler;

    private CreateJobUseCase useCase;
    private CreateJobRequest request;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        useCase = new CreateJobUseCase(jobRepository, categoryRepository, jobValidationService, jobResponseAssembler);
        clientId = UUID.randomUUID();
        request = new CreateJobRequest(
                "Cambiar interruptor",
                "Cambiar interruptor de la sala",
                UUID.randomUUID(),
                500.0,
                "USD",
                "Av. 10 de Agosto",
                "Quito",
                -0.1807,
                -78.4678
        );
    }

    @Test
    void shouldCreateJobSuccessfully() {
        when(jobValidationService.canClientCreateJob(any())).thenReturn(true);
        Category category = new Category(request.categoryId(), "ELECTRICIDAD", "desc");
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(category));

        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
            Job job = invocation.getArgument(0);
            return job;
        });

        JobResponse response = new JobResponse(
                UUID.randomUUID(), request.title(), request.description(), "ELECTRICIDAD",
                500.0, "USD", request.street(), request.city(),
                java.math.BigDecimal.valueOf(-0.1807), java.math.BigDecimal.valueOf(-78.4678),
                clientId, null, JobStatus.PUBLISHED, java.time.OffsetDateTime.now()
        );
        when(jobResponseAssembler.toResponse(any(Job.class))).thenReturn(response);

        JobResponse result = useCase.execute(request, clientId);

        assertThat(result.title()).isEqualTo(request.title());
        assertThat(result.budgetCurrency()).isEqualTo("USD");
        assertThat(result.clientId()).isEqualTo(clientId);
        assertThat(result.status()).isEqualTo(JobStatus.PUBLISHED);
    }

    @Test
    void shouldThrowForbiddenWhenClientCannotCreateJob() {
        when(jobValidationService.canClientCreateJob(any())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(request, clientId))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("not allowed to create a job");
    }

    @Test
    void shouldThrowNotFoundWhenCategoryDoesNotExist() {
        when(jobValidationService.canClientCreateJob(any())).thenReturn(true);
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, clientId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");
    }
}
