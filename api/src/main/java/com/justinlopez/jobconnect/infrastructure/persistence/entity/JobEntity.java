package com.justinlopez.jobconnect.infrastructure.persistence.entity;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "budget_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal budgetAmount;

    @Column(name = "budget_currency", nullable = false, length = 3)
    private String budgetCurrency;

    @Column()
    private String street;

    @Column(length = 100)
    private String city;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private UserEntity client;

    @Column(name = "selected_offer_id")
    private UUID selectedOfferId;

    @Column(name = "selected_professional_id")
    private UUID selectedProfessionalId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}
