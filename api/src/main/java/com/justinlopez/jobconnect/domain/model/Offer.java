package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public class Offer {

    private final UUID id;
    private final UserId professionalId;
    private final Money offeredPrice;
    private final String message;
    private OfferStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Offer(UUID id, UserId professionalId, Money offeredPrice, String message) {
        this.id = id;
        this.professionalId = professionalId;
        this.offeredPrice = offeredPrice;
        this.message = message;
        this.status = OfferStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void accept() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be accepted.");
        }
        this.status = OfferStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be rejected.");
        }
        this.status = OfferStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be withdrawn.");
        }
        this.status = OfferStatus.WITHDRAWN;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public UserId getProfessionalId() {
        return professionalId;
    }
}
