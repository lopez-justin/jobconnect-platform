package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.vo.Address;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class JobTest {



    private Job job;
    private Category category;
    private final UUID clientId = UUID.randomUUID();
    private final UUID professionalId = UUID.randomUUID();
    private final UUID jobId = UUID.randomUUID();



    @BeforeEach
    void setUp() {
        // 1. Configurar el entorno de prueba
        category = new Category(UUID.randomUUID(), "ELECTRICIDAD", "Trabajos eléctricos");
        Money budget = Money.of(500.0, "USD");
        Address address = new Address("Av. 10 de Agosto", "Quito", BigDecimal.valueOf(-0.1807), BigDecimal.valueOf(-78.4678));

        job = new Job(
                jobId,
                "Cambiar interruptor",
                "Cambiar interruptor de la sala",
                category,
                budget,
                address,
                new UserId(clientId)
        );
    }



    // ==========================================
    // 1. PRUEBAS PARA addOffer()
    // ==========================================
    @Test
    void shouldAddOfferSuccessfully() {
        // Arrange
        Offer offer = createOffer(professionalId, 400.0);

        // Act
        job.addOffer(offer);

        // Assert
        assertThat(job.getOffers()).hasSize(1);
        assertThat(job.getOffers().get(0).getProfessionalId().value()).isEqualTo(professionalId);
        assertThat(job.getStatus()).isEqualTo(JobStatus.PUBLISHED);
    }

    @Test
    void shouldThrowExceptionWhenClientTriesToOfferOnOwnJob() {
        // Arrange: El cliente intenta ofertar en su propio trabajo
        Offer offer = createOffer(clientId, 400.0);

        // Act & Assert
        assertThatThrownBy(() -> job.addOffer(offer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Client cannot make an offer on their own job.");
    }

    @Test
    void shouldThrowExceptionWhenProfessionalOffersTwice() {
        // Arrange
        Offer firstOffer = createOffer(professionalId, 400.0);
        Offer secondOffer = createOffer(professionalId, 350.0);

        // Act
        job.addOffer(firstOffer);

        // Assert
        assertThatThrownBy(() -> job.addOffer(secondOffer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Professional has already made an offer on this job.");
    }

    @Test
    void shouldThrowExceptionWhenAddingOfferToNonPublishedJob() {
        // Arrange: Primero aceptamos una oferta para que el trabajo pase a IN_PROGRESS
        Offer offerToAccept = createOffer(professionalId, 400.0);
        job.addOffer(offerToAccept);
        job.acceptOffer(offerToAccept.getId()); // Ahora está IN_PROGRESS

        // Act & Assert: Intentamos añadir otra oferta
        Offer newOffer = createOffer(UUID.randomUUID(), 300.0);
        assertThatThrownBy(() -> job.addOffer(newOffer))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot add offers to a job that is not published.");
    }



    // ==========================================
    // 2. PRUEBAS PARA acceptOffer()
    // ==========================================
    @Test
    void shouldAcceptOfferAndRejectOthers() {
        // Arrange: Añadimos 2 ofertas
        UUID professional1 = UUID.randomUUID();
        UUID professional2 = UUID.randomUUID();
        Offer offer1 = createOffer(professional1, 400.0);
        Offer offer2 = createOffer(professional2, 450.0);
        job.addOffer(offer1);
        job.addOffer(offer2);

        // Act: Aceptamos la primera oferta
        job.acceptOffer(offer1.getId());

        // Assert: Verificamos estados
        assertThat(job.getStatus()).isEqualTo(JobStatus.IN_PROGRESS);
        assertThat(job.getSelectedProfessionalId().value()).isEqualTo(professional1);

        // La oferta 1 debe estar ACCEPTED, la oferta 2 REJECTED
        assertThat(job.getOffers().get(0).getStatus()).isEqualTo(OfferStatus.ACCEPTED);
        assertThat(job.getOffers().get(1).getStatus()).isEqualTo(OfferStatus.REJECTED);
    }

    @Test
    void shouldThrowExceptionWhenAcceptingOfferNotFound() {
        // Arrange
        Offer offer = createOffer(professionalId, 400.0);
        job.addOffer(offer);
        UUID fakeOfferId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> job.acceptOffer(fakeOfferId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Offer not found for this job.");
    }

    @Test
    void shouldThrowExceptionWhenAcceptingOfferInNonPublishedJob() {
        // Arrange
        Offer offer = createOffer(professionalId, 400.0);
        job.addOffer(offer);
        job.acceptOffer(offer.getId()); // Ahora está IN_PROGRESS

        // Act & Assert: Intentamos aceptar la misma oferta de nuevo (o cualquier otra)
        // Nota: offer ya está ACCEPTED, así que lanzará error.
        assertThatThrownBy(() -> job.acceptOffer(offer.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot accept offers for a job that is not published.");
    }



    // ==========================================
    // 3. PRUEBAS PARA markAsPendingConfirmation() y confirmCompletion()
    // ==========================================
    @Test
    void shouldMarkAsPendingConfirmation() {
        // Arrange: Llevamos el trabajo a IN_PROGRESS
        Offer offer = createOffer(professionalId, 400.0);
        job.addOffer(offer);
        job.acceptOffer(offer.getId());

        // Act
        job.markAsPendingConfirmation();

        // Assert
        assertThat(job.getStatus()).isEqualTo(JobStatus.PENDING_CONFIRMATION);
    }

    @Test
    void shouldThrowExceptionWhenMarkingPendingFromInvalidState() {
        // El trabajo está en PUBLISHED (nunca se aceptó)
        assertThatThrownBy(() -> job.markAsPendingConfirmation())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only jobs IN_PROGRESS can be marked as pending");
    }

    @Test
    void shouldConfirmCompletion() {
        // Arrange: Llevar a PENDING_CONFIRMATION
        Offer offer = createOffer(professionalId, 400.0);
        job.addOffer(offer);
        job.acceptOffer(offer.getId());
        job.markAsPendingConfirmation();

        // Act
        job.confirmCompletion();

        // Assert
        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
    }

    @Test
    void shouldThrowExceptionWhenConfirmingFromInvalidState() {
        // El trabajo está en PUBLISHED
        assertThatThrownBy(() -> job.confirmCompletion())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only jobs PENDING_CONFIRMATION can be completed");
    }



    // ==========================================
    // 4. PRUEBAS PARA cancel()
    // ==========================================
    @Test
    void shouldCancelJob() {
        // Act
        job.cancel();

        // Assert
        assertThat(job.getStatus()).isEqualTo(JobStatus.CANCELED);
    }

    @Test
    void shouldThrowExceptionWhenCancelingCompletedJob() {
        // Arrange: Llevar a COMPLETED
        Offer offer = createOffer(professionalId, 400.0);
        job.addOffer(offer);
        job.acceptOffer(offer.getId());
        job.markAsPendingConfirmation();
        job.confirmCompletion();

        // Act & Assert
        assertThatThrownBy(() -> job.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel a job that is already completed or canceled.");
    }




    // ==========================================
    // PARA CREAR OFERTAS
    // ==========================================
    private Offer createOffer(UUID professionalId, double amount) {
        Money price = Money.of(amount, "USD");
        return new Offer(
                UUID.randomUUID(),
                jobId,
                new UserId(professionalId),
                price,
                "Oferta de prueba"
        );
    }


}