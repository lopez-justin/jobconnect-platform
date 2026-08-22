package com.justinlopez.jobconnect.infrastructure.persistence.adapter;

import com.justinlopez.jobconnect.domain.model.Transaction;
import com.justinlopez.jobconnect.domain.repository.TransactionRepository;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.TransactionEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.mapper.TransactionMapper;
import com.justinlopez.jobconnect.infrastructure.persistence.repository.JpaTransactionRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaTransactionRepository implements TransactionRepository {

    private final JpaTransactionRepositoryInterface jpaTransaction;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = this.transactionMapper.toEntity(transaction);
        TransactionEntity savedEntity = this.jpaTransaction.save(entity);
        return this.transactionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findByJobId(UUID jobId) {
        return this.jpaTransaction.findByJobId(jobId)
                .map(this.transactionMapper::toDomain);
    }
}
