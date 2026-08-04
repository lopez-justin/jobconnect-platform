package com.justinlopez.jobconnect.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilidades para la generación y validación de JSON Web Tokens (JWT)
 * usando criptografía asimétrica RSA (clave privada para firmar,
 * clave pública para verificar)
 *
 * @author Justin Lopez
 */
@Slf4j
@Component
public class JwtSecurityUtils {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    /**
     * Construye una instancia de JwtSecurityUtils cargando las claves RSA desde archivos PEM
     * y configurando los tiempos de expiración para los access y refresh token.
     * @param accessTokenExpiration tiempo de expiración del access token
     * @param refreshTokenExpiration tiempo de expiración del refresh token
     */
    public JwtSecurityUtils(
            @Value("${app.security.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${app.security.jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.privateKey = KeyUtils.loadPrivateKey();
        this.publicKey = KeyUtils.loadPublicKey();
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Genera un access token
     * @param authentication objeto con la información del usuario autenticado
     * @return access token
     */
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, "access", accessTokenExpiration, true);
    }

    /**
     * Genera un refresh token
     * @param authentication objeto con la información del usuario autenticado
     * @return refresh token
     */
    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, "refresh", refreshTokenExpiration, false);
    }

    /**
     * Genera un token JWT con los claims especificados.
     * @param authentication objeto con la información del usuario autenticado
     * @param tokenType tipo del token (access - refresh)
     * @param expirationMillis tiempo de expiración del token en milisegundos
     * @param includeRoles bandera para incluir los roles o no
     * @return token
     */
    private String createToken(Authentication authentication, String tokenType, long expirationMillis, boolean includeRoles) {
        final Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("token_type", tokenType);

        if (includeRoles) {
            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            claims.put("roles", authorities);
        }

        Instant now = Instant.now();
        Instant expiration = now.plus(expirationMillis, ChronoUnit.MILLIS);

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claims(claims)
                .signWith(privateKey)
                .compact();
    }

    /**
     * Valida firma, expiración del token
     * @param token token a validar
     * @return true si el token es valido, de lo contrario false
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT exception: {}", e.getMessage());
        }
        return false;
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(Date.from(Instant.now()));
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
