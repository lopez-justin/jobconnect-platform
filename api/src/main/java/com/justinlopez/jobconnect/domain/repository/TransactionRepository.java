package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findByJobId(UUID jobId);
    Page<Transaction> findByClientId(UUID clientId, Pageable pageable);
    Page<Transaction> findByProfessionalId(UUID professionalId, Pageable pageable);
}
