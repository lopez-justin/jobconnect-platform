package com.justinlopez.jobconnect.infrastructure.web.controller;

import com.justinlopez.jobconnect.application.dto.request.CreateOfferRequest;
import com.justinlopez.jobconnect.application.dto.response.OfferResponse;
import com.justinlopez.jobconnect.application.service.CreateOfferUseCase;
import com.justinlopez.jobconnect.infrastructure.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OfferController {

    private final CreateOfferUseCase createOfferUseCase;

    @Operation(
            summary = "Create a new offer",
            description = "Allows an authenticated professional to submit a new offer for a job."
    )
    @PostMapping
    @PreAuthorize("hasRole('PROFESSIONAL')")
    public ResponseEntity<OfferResponse> createOffer(
            @Valid @RequestBody CreateOfferRequest request,
            @AuthenticationPrincipal CustomUserDetailsService.UserWithId userDetails
    ) {
        var offerResponse = createOfferUseCase.execute(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(offerResponse);
    }

}
