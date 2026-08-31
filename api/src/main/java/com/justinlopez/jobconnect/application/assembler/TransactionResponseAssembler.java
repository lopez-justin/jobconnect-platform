package com.justinlopez.jobconnect.application.assembler;

import com.justinlopez.jobconnect.application.dto.response.TransactionResponse;
import com.justinlopez.jobconnect.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionResponseAssembler {

    public TransactionResponse toResponse(Transaction transaction, String jobTitle) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getJobId(),
                jobTitle,
                transaction.getAmount().amount().doubleValue(),
                transaction.getAmount().currency(),
                transaction.getStatus(),
                transaction.getStripePaymentIntentId(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
