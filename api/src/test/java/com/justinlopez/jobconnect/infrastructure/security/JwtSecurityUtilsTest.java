package com.justinlopez.jobconnect.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtSecurityUtilsTest {

    private JwtSecurityUtils jwtSecurityUtils;

    private Authentication authentication;
    private UUID userId;
    private String username;

    @BeforeEach
    void setUp() throws Exception {
        jwtSecurityUtils = new JwtSecurityUtils(3600000L, 604800000L);

        userId = UUID.randomUUID();
        username = "user@example.com";
        CustomUserDetailsService.UserWithId principal =
                new CustomUserDetailsService.UserWithId(
                        username,
                        "",
                        List.of(new SimpleGrantedAuthority("CLIENT")),
                        userId
                );

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority("CLIENT")));
    }

    @Test
    void createAccessToken_shouldProduceValidAccessToken() {
        String token = jwtSecurityUtils.createAccessToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(jwtSecurityUtils.isTokenValid(token)).isTrue();
        assertThat(jwtSecurityUtils.isAccessToken(token)).isTrue();
        assertThat(jwtSecurityUtils.isRefreshToken(token)).isFalse();
        assertThat(jwtSecurityUtils.extractSubject(token)).isEqualTo(username);
        assertThat(jwtSecurityUtils.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtSecurityUtils.extractRoles(token)).containsExactly("CLIENT");
        assertThat(jwtSecurityUtils.extractAuthorities(token))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("CLIENT");
    }

    @Test
    void createRefreshToken_shouldProduceValidRefreshToken() {
        String token = jwtSecurityUtils.createRefreshToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(jwtSecurityUtils.isTokenValid(token)).isTrue();
        assertThat(jwtSecurityUtils.isRefreshToken(token)).isTrue();
        assertThat(jwtSecurityUtils.isAccessToken(token)).isFalse();
        assertThat(jwtSecurityUtils.extractSubject(token)).isEqualTo(username);
        assertThat(jwtSecurityUtils.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtSecurityUtils.extractRoles(token)).isEmpty();
    }

    @Test
    void extractExpiration_shouldReturnFutureDate() {
        String token = jwtSecurityUtils.createAccessToken(authentication);

        Date expiration = jwtSecurityUtils.extractExpiration(token);
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void isTokenValid_shouldRejectTamperedToken() {
        String token = jwtSecurityUtils.createAccessToken(authentication);

        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtSecurityUtils.isTokenValid(tampered)).isFalse();
    }

    @Test
    void isTokenValid_shouldRejectGarbage() {
        assertThat(jwtSecurityUtils.isTokenValid("not.a.jwt")).isFalse();
    }

    @Test
    void extractUserId_shouldReturnNullWhenClaimMissing() {
        org.springframework.security.core.userdetails.User plainPrincipal =
                new org.springframework.security.core.userdetails.User(
                        username,
                        "",
                        List.of(new SimpleGrantedAuthority("CLIENT"))
                );

        Authentication noIdAuth = mock(Authentication.class);
        when(noIdAuth.getPrincipal()).thenReturn(plainPrincipal);
        when(noIdAuth.getName()).thenReturn(username);
        when(noIdAuth.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority("CLIENT")));

        String token = jwtSecurityUtils.createAccessToken(noIdAuth);

        assertThat(jwtSecurityUtils.isTokenValid(token)).isTrue();
        assertThat(jwtSecurityUtils.extractUserId(token)).isNull();
    }

    @Test
    void extractRoles_shouldHandleMultipleRoles() {
        GrantedAuthority client = new SimpleGrantedAuthority("CLIENT");
        GrantedAuthority professional = new SimpleGrantedAuthority("PROFESSIONAL");

        CustomUserDetailsService.UserWithId principal =
                new CustomUserDetailsService.UserWithId(
                        username,
                        "",
                        List.of(client, professional),
                        userId
                );

        Authentication multi = mock(Authentication.class);
        when(multi.getPrincipal()).thenReturn(principal);
        when(multi.getName()).thenReturn(username);
        when(multi.getAuthorities()).thenAnswer(inv -> List.of(client, professional));

        String token = jwtSecurityUtils.createAccessToken(multi);

        assertThat(jwtSecurityUtils.extractRoles(token))
                .containsExactlyInAnyOrder("CLIENT", "PROFESSIONAL");
    }
}
