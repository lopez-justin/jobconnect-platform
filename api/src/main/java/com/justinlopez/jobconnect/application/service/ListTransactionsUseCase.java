package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.dto.response.TransactionResponse;
import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.Transaction;
import com.justinlopez.jobconnect.domain.repository.JobRepository;
import com.justinlopez.jobconnect.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;
    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> listTransactions(UUID userId, String role, int page, int size) {
        log.info("Listing transactions for userId: {} role: {}", userId, role);

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionsPage;
        if ("PROFESSIONAL".equalsIgnoreCase(role)) {
            transactionsPage = this.transactionRepository.findByProfessionalId(userId, pageable);
        } else {
            transactionsPage = this.transactionRepository.findByClientId(userId, pageable);
        }

        Map<UUID, String> jobTitles = loadJobTitles(transactionsPage.getContent());

        List<TransactionResponse> responses = transactionsPage.getContent().stream()
                .map(transaction -> mapToResponse(transaction, jobTitles))
                .toList();

        return new PageImpl<>(responses, pageable, transactionsPage.getTotalElements());
    }

    private Map<UUID, String> loadJobTitles(List<Transaction> transactions) {
        Set<UUID> jobIds = transactions.stream()
                .map(Transaction::getJobId)
                .collect(Collectors.toSet());

        if (jobIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return this.jobRepository.findByIds(jobIds).stream()
                .collect(Collectors.toMap(Job::getId, Job::getTitle, (a, b) -> a));
    }

    private TransactionResponse mapToResponse(Transaction transaction, Map<UUID, String> jobTitles) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getJobId(),
                jobTitles.get(transaction.getJobId()),
                transaction.getAmount().amount().doubleValue(),
                transaction.getAmount().currency(),
                transaction.getStatus(),
                transaction.getStripePaymentIntentId(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

}
