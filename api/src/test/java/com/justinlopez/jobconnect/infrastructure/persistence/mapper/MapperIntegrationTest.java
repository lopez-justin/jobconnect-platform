package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.AbstractIntegrationTest;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.Transaction;
import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.enums.TransactionStatus;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.CategoryEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.JobEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.OfferEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.TransactionEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.UserEntity;
import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MapperIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private TransactionMapper transactionMapper;

    private UserEntity userEntity() {
        UserEntity u = new UserEntity();
        u.setId(UUID.randomUUID());
        u.setEmail("pro@test.com");
        u.setFullName("Pro Test");
        return u;
    }

    @Test
    void jobToDomain_shouldMapAggregate() {
        CategoryEntity category = new CategoryEntity();
        category.setId(UUID.randomUUID());
        category.setName("ELECTRICIDAD");

        JobEntity entity = new JobEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Cambiar interruptor");
        entity.setDescription("Descripcion");
        entity.setCategory(category);
        entity.setBudgetAmount(new BigDecimal("500.00"));
        entity.setBudgetCurrency("ARS");
        entity.setStreet("Av. Siempre Viva");
        entity.setCity("Buenos Aires");
        entity.setLatitude(new BigDecimal("-34.60000000"));
        entity.setLongitude(new BigDecimal("-58.40000000"));
        entity.setClient(userEntity());
        entity.setSelectedProfessionalId(UUID.randomUUID());
        entity.setStatus(JobStatus.PUBLISHED);
        entity.setCreatedAt(OffsetDateTime.now());

        Job job = jobMapper.toDomain(entity);

        assertThat(job.getTitle()).isEqualTo("Cambiar interruptor");
        assertThat(job.getBudget().amount()).isEqualByComparingTo("500.00");
        assertThat(job.getBudget().currency()).isEqualTo("ARS");
        assertThat(job.getLocation().city()).isEqualTo("Buenos Aires");
        assertThat(job.getClientId()).isNotNull();
        assertThat(job.getSelectedProfessionalId()).isNotNull();
        assertThat(job.getStatus()).isEqualTo(JobStatus.PUBLISHED);
        assertThat(job.getCategory().getName()).isEqualTo("ELECTRICIDAD");
    }

    @Test
    void offerToDomain_shouldMapAllFields() {
        OfferEntity entity = new OfferEntity();
        entity.setId(UUID.randomUUID());
        entity.setJob(new JobEntity());
        entity.getJob().setId(UUID.randomUUID());
        entity.setProfessional(userEntity());
        entity.setOfferedPriceAmount(new BigDecimal("400.00"));
        entity.setOfferedPriceCurrency("ARS");
        entity.setMessage("Oferta de prueba");
        entity.setStatus(OfferStatus.PENDING);
        entity.setCreatedAt(OffsetDateTime.now());

        Offer offer = offerMapper.toDomain(entity);

        assertThat(offer.getId()).isEqualTo(entity.getId());
        assertThat(offer.getJobId()).isEqualTo(entity.getJob().getId());
        assertThat(offer.getProfessionalId().value()).isEqualTo(entity.getProfessional().getId());
        assertThat(offer.getOfferedPrice().amount()).isEqualByComparingTo("400.00");
        assertThat(offer.getOfferedPrice().currency()).isEqualTo("ARS");
        assertThat(offer.getStatus()).isEqualTo(OfferStatus.PENDING);
    }

    @Test
    void transactionToDomain_shouldMapAmountAndCurrency() {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(UUID.randomUUID());
        entity.setJobId(UUID.randomUUID());
        entity.setClientId(UUID.randomUUID());
        entity.setProfessionalId(UUID.randomUUID());
        entity.setAmount(new BigDecimal("700.00"));
        entity.setCurrency("USD");
        entity.setStatus(TransactionStatus.CAPTURED);
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());

        Transaction transaction = transactionMapper.toDomain(entity);

        assertThat(transaction.getId()).isEqualTo(entity.getId());
        assertThat(transaction.getClientId().value()).isEqualTo(entity.getClientId());
        assertThat(transaction.getProfessionalId().value()).isEqualTo(entity.getProfessionalId());
        assertThat(transaction.getAmount().amount()).isEqualByComparingTo("700.00");
        assertThat(transaction.getAmount().currency()).isEqualTo("USD");
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.CAPTURED);
    }

    @Test
    void jobToEntityAndBack_shouldRoundTripMoney() {
        Job job = new Job(
                null,
                "Pintar",
                "desc",
                new com.justinlopez.jobconnect.domain.model.Category(UUID.randomUUID(), "PINTURA", "desc"),
                new Money(new BigDecimal("1000.00"), "ARS"),
                new com.justinlopez.jobconnect.domain.model.vo.Address(
                        "Calle 1", "CABA", new BigDecimal("-34.6"), new BigDecimal("-58.4")),
                new UserId(UUID.randomUUID())
        );

        JobEntity entity = jobMapper.toEntity(job);
        assertThat(entity.getBudgetAmount()).isEqualByComparingTo("1000.00");
        assertThat(entity.getBudgetCurrency()).isEqualTo("ARS");

        Job back = jobMapper.toDomain(entity);
        assertThat(back.getBudget().currency()).isEqualTo("ARS");
        assertThat(back.getLocation().street()).isEqualTo("Calle 1");
    }

    @Test
    void jobToEntity_shouldMapOffersCollection() {
        Job job = new Job(
                null,
                "Remodelar",
                "desc",
                new com.justinlopez.jobconnect.domain.model.Category(UUID.randomUUID(), "CARPINTERIA", "desc"),
                Money.of(1500.0, "USD"),
                new com.justinlopez.jobconnect.domain.model.vo.Address(
                        "Calle 2", "Quito", new BigDecimal("-0.18"), new BigDecimal("-78.4")),
                new UserId(UUID.randomUUID())
        );
        job.addOffer(new Offer(null, job.getId(), new UserId(UUID.randomUUID()), Money.of(1200.0, "USD"), "Oferta"));

        JobEntity entity = jobMapper.toEntity(job);

        assertThat(entity.getOffers()).hasSize(1);
        assertThat(entity.getOffers().get(0).getOfferedPriceCurrency()).isEqualTo("USD");
    }
}
