package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.JobResponseAssembler;
import com.justinlopez.jobconnect.application.dto.response.JobResponse;
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
import org.mockito.ArgumentCaptor;
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
class AcceptOfferUseCaseTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private JobResponseAssembler jobResponseAssembler;

    private AcceptOfferUseCase useCase;
    private UUID clientId;
    private UUID jobId;
    private Job job;
    private Offer offer;

    @BeforeEach
    void setUp() {
        useCase = new AcceptOfferUseCase(jobRepository, transactionRepository, jobResponseAssembler);
        clientId = UUID.randomUUID();
        jobId = UUID.randomUUID();
        job = new Job(
                jobId,
                "Cambiar interruptor",
                "desc",
                new Category(UUID.randomUUID(), "ELECTRICIDAD", "desc"),
                Money.of(500.0, "USD"),
                new Address("Av. 10 de Agosto", "Quito", BigDecimal.valueOf(-0.1807), BigDecimal.valueOf(-78.4678)),
                new UserId(clientId)
        );
        offer = new Offer(
                UUID.randomUUID(), jobId, new UserId(UUID.randomUUID()),
                Money.of(400.0, "USD"), "Oferta"
        );
        job.addOffer(offer);
    }

    @Test
    void shouldAcceptOfferCreateCapturedTransactionAndSaveJob() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobResponseAssembler.toResponse(any(Job.class))).thenAnswer(invocation -> {
            Job saved = invocation.getArgument(0);
            return new JobResponse(
                    saved.getId(), saved.getTitle(), saved.getDescription(), saved.getCategory().getName(),
                    saved.getBudget().amount().doubleValue(), saved.getBudget().currency(),
                    saved.getLocation().street(), saved.getLocation().city(),
                    saved.getLocation().latitude(), saved.getLocation().longitude(),
                    saved.getClientId().value(), saved.getSelectedProfessionalId() != null ? saved.getSelectedProfessionalId().value() : null,
                    saved.getStatus(), saved.getCreatedAt()
            );
        });

        useCase.execute(jobId, offer.getId(), clientId);

        assertThat(job.getStatus()).isEqualTo(JobStatus.IN_PROGRESS);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        Transaction tx = txCaptor.getValue();
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.CAPTURED);
        assertThat(tx.getJobId()).isEqualTo(jobId);
        assertThat(tx.getAmount().currency()).isEqualTo("USD");
        assertThat(tx.getProfessionalId().value()).isEqualTo(offer.getProfessionalId().value());
        verify(jobRepository).save(job);
    }

    @Test
    void shouldThrowWhenJobNotFound() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(jobId, offer.getId(), clientId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Job not found");
    }

    @Test
    void shouldThrowForbiddenWhenClientIsNotOwner() {
        Job otherJob = job;
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(otherJob));
        UUID otherClient = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.execute(jobId, offer.getId(), otherClient))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("not the owner");
    }
}
