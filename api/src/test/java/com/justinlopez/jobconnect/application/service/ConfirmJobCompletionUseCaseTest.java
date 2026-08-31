package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobResponseAssembler;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
import com.justinlopez.jobconnect.application.exception.ConflictException;
import com.justinlopez.jobconnect.application.exception.ForbiddenOperationException;
import com.justinlopez.jobconnect.application.exception.ResourceNotFoundException;
import com.justinlopez.jobconnect.domain.model.Category;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.Transaction;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.enums.TransactionStatus;
import com.justinlopez.jobconnect.domain.model.vo.Address;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmJobCompletionUseCaseTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private JobResponseAssembler jobResponseAssembler;

    private ConfirmJobCompletionUseCase useCase;
    private UUID clientId;
    private UUID jobId;
    private UUID professionalId;
    private Job job;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        useCase = new ConfirmJobCompletionUseCase(jobRepository, transactionRepository, jobResponseAssembler);
        clientId = UUID.randomUUID();
        jobId = UUID.randomUUID();
        professionalId = UUID.randomUUID();

        job = new Job(
                jobId, "Instalar cerámica", "desc",
                new Category(UUID.randomUUID(), "PLOMERIA", "desc"),
                Money.of(800.0, "USD"),
                new Address("Calle 10", "Quito", BigDecimal.valueOf(-0.18), BigDecimal.valueOf(-78.4)),
                new UserId(clientId)
        );
        Offer offer = new Offer(UUID.randomUUID(), jobId, new UserId(professionalId), Money.of(700.0, "USD"), "Oferta");
        job.addOffer(offer);
        job.acceptOffer(offer.getId());
        job.markAsPendingConfirmation();

        transaction = new Transaction(UUID.randomUUID(), jobId, new UserId(clientId), new UserId(professionalId), Money.of(700.0, "USD"));
        transaction.capture();
    }

    @Test
    void shouldConfirmCompletionAndReleaseTransaction() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(transactionRepository.findByJobId(jobId)).thenReturn(Optional.of(transaction));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(jobId, clientId);

        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.RELEASED);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldThrowForbiddenWhenClientIsNotOwner() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        assertThatThrownBy(() -> useCase.execute(jobId, UUID.randomUUID()))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("not the owner");
    }

    @Test
    void shouldThrowConflictWhenJobNotPendingConfirmation() {
        Job published = new Job(
                jobId, "Otra", "desc",
                new Category(UUID.randomUUID(), "PINTURA", "desc"),
                Money.of(500.0, "USD"),
                new Address("Calle 20", "Quito", BigDecimal.valueOf(-0.2), BigDecimal.valueOf(-78.4)),
                new UserId(clientId)
        );
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(published));

        assertThatThrownBy(() -> useCase.execute(jobId, clientId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("PENDING_CONFIRMATION");
    }

    @Test
    void shouldThrowNotFoundWhenTransactionMissing() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(transactionRepository.findByJobId(jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(jobId, clientId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found");
    }
}
