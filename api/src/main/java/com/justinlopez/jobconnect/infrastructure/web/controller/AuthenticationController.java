package com.justinlopez.jobconnect.infrastructure.web.controller;

import com.justinlopez.jobconnect.application.dto.request.LoginRequest;
import com.justinlopez.jobconnect.application.dto.request.RegisterRequest;
import com.justinlopez.jobconnect.application.dto.response.AuthenticationResponse;
import com.justinlopez.jobconnect.application.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Authenticate a user",
            description = "Allows a user to log in and receive a JWT token for authentication."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("REST request to authenticate an user");
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Register a new user",
            description = "Allows a new user to register and receive a JWT token for authentication."
    )
    @PostMapping("register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("REST request to register a new user");
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
