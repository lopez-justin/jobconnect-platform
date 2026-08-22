package com.justinlopez.jobconnect.domain.repository;

import com.justinlopez.jobconnect.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findByJobId(UUID jobId);
}
