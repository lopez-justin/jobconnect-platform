package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Offer {

    private final UUID id;
    private final UUID jobId;
    private final UserId professionalId;
    private final Money offeredPrice;
    private final String message;
    private OfferStatus status;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Offer(UUID id, UUID jobId, UserId professionalId, Money offeredPrice, String message) {
        this.id = id;
        this.jobId = jobId;
        this.professionalId = professionalId;
        this.offeredPrice = offeredPrice;
        this.message = message;
        this.status = OfferStatus.PENDING;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public void accept() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be accepted.");
        }
        this.status = OfferStatus.ACCEPTED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void reject() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be rejected.");
        }
        this.status = OfferStatus.REJECTED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void withdraw() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be withdrawn.");
        }
        this.status = OfferStatus.WITHDRAWN;
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getJobId() {
        return jobId;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public UserId getProfessionalId() {
        return professionalId;
    }

    public Money getOfferedPrice() {
        return offeredPrice;
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

}
