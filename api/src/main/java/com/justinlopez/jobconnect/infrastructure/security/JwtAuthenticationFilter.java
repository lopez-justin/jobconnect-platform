package com.justinlopez.jobconnect.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtSecurityUtils jwtSecurityUtils;

    private static final String BEARER_PREFIX = "Bearer ";

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(BEARER_PREFIX.length());

        if (!jwtSecurityUtils.isTokenValid(token)) {
            log.debug("Invalid token or expired, request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Un refresh token JAMÁS debe usarse para autenticar peticiones normales, solo para el endpoint de /refresh-token.
        if (!jwtSecurityUtils.isAccessToken(token)) {
            log.warn("An attempt was made to use a token of a different type than ACCESS in {}", request.getRequestURI());
            rejectAsUnauthorized(response, "Invalid token type");
            return;
        }

        // Evita re-autenticar si ya existe una autenticación en el contexto
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                authenticateRequest(token, request);
            } catch (RuntimeException e) {
                log.error("Error processing JWT: {}", e.getMessage(), e);
                rejectAsUnauthorized(response, "It was not possible to validate the credentials");
                return;
            }
        }

        filterChain.doFilter(request, response);

    }

    private void authenticateRequest(String token, HttpServletRequest request) {
        String username = jwtSecurityUtils.extractSubject(token);
        List<SimpleGrantedAuthority> authorities = jwtSecurityUtils.extractAuthorities(token);
        UUID userId = jwtSecurityUtils.extractUserId(token);

        if (userId == null) {
            throw new IllegalArgumentException("Missing user_id claim in JWT");
        }

        CustomUserDetailsService.UserWithId principal =
                new CustomUserDetailsService.UserWithId(username, "", authorities, userId);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        log.debug("User {} authenticated with authorities: {} and userId: {}", username, authorities, userId);
    }

    private void rejectAsUnauthorized(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message)
        );
    }

}
