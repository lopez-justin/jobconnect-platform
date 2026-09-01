package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.TransactionStatus;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final UUID jobId;
    private final UserId clientId;
    private final UserId professionalId;
    private final Money amount;
    private TransactionStatus status;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Transaction(UUID id, UUID jobId, UserId clientId, UserId professionalId, Money amount) {
        this.id = id;
        this.jobId = jobId;
        this.clientId = clientId;
        this.professionalId = professionalId;
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }


    public void release() {
        if (this.status != TransactionStatus.CAPTURED) {
            throw new IllegalStateException("Only CAPTURED transactions can be released");
        }
        this.status = TransactionStatus.RELEASED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void capture() {
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transactions can be captured");
        }
        this.status = TransactionStatus.CAPTURED;
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getJobId() {
        return jobId;
    }

    public UserId getClientId() {
        return clientId;
    }

    public UserId getProfessionalId() {
        return professionalId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}