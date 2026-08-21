package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.TransactionStatus;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final UUID jobId;
    private final UserId clientId;
    private final UserId professionalId;
    private final Money amount;
    private final String stripePaymentIntentId;
    private TransactionStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Transaction(UUID id, UUID jobId, UserId clientId, UserId professionalId, Money amount, String stripePaymentIntentId) {
        this.id = id;
        this.jobId = jobId;
        this.clientId = clientId;
        this.professionalId = professionalId;
        this.amount = amount;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public void release() {
        if (this.status != TransactionStatus.CAPTURED) {
            throw new IllegalStateException("Only CAPTURED transactions can be released");
        }
        this.status = TransactionStatus.RELEASED;
        this.updatedAt = LocalDateTime.now();
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

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}