package com.justinlopez.jobconnect.infrastructure.persistence.repository;

import com.justinlopez.jobconnect.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaTransactionRepositoryInterface extends JpaRepository<TransactionEntity, UUID> {
    Optional<TransactionEntity> findByJobId(UUID jobId);
}
