package com.justinlopez.jobconnect.infrastructure.web.controller;

import com.justinlopez.jobconnect.application.dto.response.TransactionResponse;
import com.justinlopez.jobconnect.application.service.ListTransactionsUseCase;
import com.justinlopez.jobconnect.infrastructure.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ListTransactionsUseCase listTransactionsUseCase;

    @Operation(
            summary = "List my transactions",
            description = "Allows an authenticated client or professional to list their own payment transactions."
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TransactionResponse>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetailsService.UserWithId userDetails) {

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_CLIENT")
                .replace("ROLE_", "");

        Page<TransactionResponse> response = this.listTransactionsUseCase.listTransactions(
                userDetails.getUserId(),
                role,
                page,
                size
        );

        return ResponseEntity.ok(response);
    }

}
