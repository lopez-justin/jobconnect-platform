package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.assembler.OfferResponseAssembler;
import com.justinlopez.jobconnect.application.dto.request.CreateOfferRequest;
import com.justinlopez.jobconnect.application.dto.response.OfferResponse;
import com.justinlopez.jobconnect.application.exception.ForbiddenOperationException;
import com.justinlopez.jobconnect.application.exception.ResourceNotFoundException;
import com.justinlopez.jobconnect.domain.model.Category;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.vo.Address;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.repository.OfferRepository;
import com.justinlopez.jobconnect.domain.service.OfferValidationService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOfferUseCaseTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private OfferValidationService offerValidationService;
    @Mock
    private OfferResponseAssembler offerResponseAssembler;

    private CreateOfferUseCase useCase;
    private UUID jobId;
    private UUID professionalId;
    private Job jobInArs;

    @BeforeEach
    void setUp() {
        useCase = new CreateOfferUseCase(jobRepository, offerRepository, offerValidationService, offerResponseAssembler);
        jobId = UUID.randomUUID();
        professionalId = UUID.randomUUID();
        // Job with a non-USD currency to verify the offer inherits it
        jobInArs = new Job(
                jobId,
                "Pintar casa",
                "desc",
                new Category(UUID.randomUUID(), "PINTURA", "desc"),
                new Money(new BigDecimal("1000.00"), "ARS"),
                new Address("Av. Siempre Viva", "Buenos Aires", BigDecimal.valueOf(-34.6), BigDecimal.valueOf(-58.4)),
                new UserId(UUID.randomUUID())
        );
    }

    @Test
    void shouldCreateOfferUsingJobCurrency() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(jobInArs));
        when(offerValidationService.canProfessionalOffer(any())).thenReturn(true);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(offerResponseAssembler.toResponse(any(Offer.class))).thenAnswer(invocation -> {
            Offer o = invocation.getArgument(0);
            return new OfferResponse(
                    o.getId(), o.getJobId(), o.getProfessionalId().value(), null,
                    o.getOfferedPrice().amount().doubleValue(), o.getOfferedPrice().currency(),
                    o.getMessage(), o.getStatus(), o.getCreatedAt()
            );
        });

        OfferResponse response = useCase.execute(new CreateOfferRequest(jobId, 750.0, "Oferta"), professionalId);

        assertThat(response.currency()).isEqualTo("ARS");
        assertThat(jobInArs.getOffers()).hasSize(1);
        assertThat(jobInArs.getOffers().get(0).getOfferedPrice().currency()).isEqualTo("ARS");
    }

    @Test
    void shouldThrowForbiddenWhenProfessionalCannotOffer() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(jobInArs));
        when(offerValidationService.canProfessionalOffer(any())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new CreateOfferRequest(jobId, 750.0, "Oferta"), professionalId))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("not allowed to make offers");
    }

    @Test
    void shouldThrowNotFoundWhenJobDoesNotExist() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new CreateOfferRequest(jobId, 750.0, "Oferta"), professionalId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Job not found");
    }
}
