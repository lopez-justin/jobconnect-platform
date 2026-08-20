package com.justinlopez.jobconnect.domain.model.vo;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {

    public Money {
        // Validate that amount is non-negative and currency is not null or empty
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        // Validate that currency is not null or empty
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be null or empty");
        }
        // Validate that currency is a 3-letter ISO code
        if (currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO code (e.g., USD)");
        }
    }

    // Factory method to create a Money instance from a double amount and a currency string
    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency.toUpperCase());
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add Money with different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
