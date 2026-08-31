package com.justinlopez.jobconnect.application.port.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Puerto de la capa de aplicación para la emisión, validación y extracción
 * de claims de los JWT. La infraestructura provee la implementación real.
 */
public interface TokenProvider {

    String createAccessToken(Authentication authentication);

    String createRefreshToken(Authentication authentication);

    boolean isTokenValid(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    String extractSubject(String token);

    Date extractExpiration(String token);

    UUID extractUserId(String token);

    List<SimpleGrantedAuthority> extractAuthorities(String token);
}
