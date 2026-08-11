package com.justinlopez.jobconnect.application.service;

import com.justinlopez.jobconnect.application.dto.request.LoginRequest;
import com.justinlopez.jobconnect.application.dto.request.RegisterRequest;
import com.justinlopez.jobconnect.application.dto.response.AuthenticationResponse;
import com.justinlopez.jobconnect.domain.model.User;
import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;
import com.justinlopez.jobconnect.domain.model.vo.Email;
import com.justinlopez.jobconnect.domain.repository.RoleRepository;
import com.justinlopez.jobconnect.domain.repository.UserRepository;
import com.justinlopez.jobconnect.infrastructure.security.JwtSecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Handles authentication and registration flows for users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtSecurityUtils jwtSecurityUtils;

    /**
     * Authenticates an existing user and returns access and refresh tokens.
     *
     * @param request login credentials
     * @return authenticated user response
     */
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user with email: {}", request.email());

        try {
            // 1. Autenticar usando Spring Security
            Authentication authentication = authenticateUser(request.email(), request.password());

            // 2. Generar token JWT
            final String accessToken = jwtSecurityUtils.createAccessToken(authentication);
            final String refreshToken = jwtSecurityUtils.createRefreshToken(authentication);

            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalStateException("User not found after successful authentication"));

            return buildAuthenticationResponse(user, accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            log.error("Error occurred while authenticating user", e);
            throw new BadCredentialsException("Invalid email or password");
        }

    }

    /**
     * Registers a new user, then authenticates the created account.
     *
     * @param request registration data
     * @return authenticated user response
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        checkUserEmail(request.email());
        checkPasswords(request.password(), request.confirmPassword());
        if (request.phone() != null) {
            checkUserPhoneNumber(request.phone());
        }

        // Validar que el rol sea permitido para registro (CLIENT o PROFESSIONAL)
        UserRoleName requestedRole = request.role();
        if (requestedRole == UserRoleName.ADMIN) {
            throw new IllegalArgumentException("Cannot register with ADMIN role");
        }

        // Obtener el rol
        var role = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + requestedRole));

        // Crear usuario de dominio
        User newUser = new User(
                null,
                new Email(request.email()),
                request.fullName(),
                passwordEncoder.encode(request.password()),
                request.phone(),
                true,
                Set.of(role)
        );

        // Guardar en bd
        User savedUser = this.userRepository.save(newUser);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Autenticar al usuario
        try {
            Authentication authentication = authenticateUser(request.email(), request.password());

            // Generar token JWT
            final String accessToken = jwtSecurityUtils.createAccessToken(authentication);
            final String refreshToken = jwtSecurityUtils.createRefreshToken(authentication);

            return buildAuthenticationResponse(savedUser, accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            log.error("Error occurred while authenticating newly registered user", e);
            throw new BadCredentialsException("Invalid email or password after registration");
        }

    }

    /**
     * Authenticates the provided credentials through Spring Security.
     *
     * @param email user email
     * @param password user password
     * @return authenticated principal
     */
    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authToken);
    }

    /**
     * Builds the API response returned after authentication or registration.
     *
     * @param user authenticated user
     * @param accessToken JWT access token
     * @param refreshToken JWT refresh token
     * @return authentication response payload
     */
    private AuthenticationResponse buildAuthenticationResponse(User user, String accessToken, String refreshToken) {
        return AuthenticationResponse.builder()
                .userId(user.getId())
                .email(user.getEmail().value())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(role -> role.getName().name()).toList())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Ensures the email is not already registered.
     *
     * @param email email to validate
     */
    private void checkUserEmail(final String email) {
        final boolean emailExists = this.userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    /**
     * Ensures the password and confirmation password match.
     *
     * @param password password value
     * @param confirmPassword confirmation password value
     */
    private void checkPasswords(final String password, final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }

    /**
     * Ensures the phone number is not already registered.
     *
     * @param phone phone number to validate
     */
    private void checkUserPhoneNumber(final String phone) {
        final boolean phoneNumberExists = this.userRepository.existsByPhone(phone);
        if (phoneNumberExists) {
            throw new IllegalArgumentException("A user with this phone number already exists: " + phone);
        }
    }

}
