package com.justinlopez.jobconnect.infrastructure.security;

import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Helpers para extraer el rol principal del usuario autenticado de forma tipada.
 */
public final class SecurityRoles {

    private static final String ROLE_PREFIX = "ROLE_";

    private SecurityRoles() {
    }

    /**
     * Extrae el primer rol conocido presente en las autoridades del principal.
     *
     * @param authorities autoridades del principal autenticado
     * @return rol tipado o null si ninguno es reconocible
     */
    public static UserRoleName resolveRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith(ROLE_PREFIX))
                .map(auth -> auth.substring(ROLE_PREFIX.length()))
                .map(SecurityRoles::fromStringSafe)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Extrae el rol principal y lanza una excepción si no se encuentra uno válido.
     *
     * @param authorities autoridades del principal autenticado
     * @return rol tipado
     */
    public static UserRoleName requireRole(Collection<? extends GrantedAuthority> authorities) {
        UserRoleName role = resolveRole(authorities);
        if (role == null) {
            throw new IllegalStateException("No supported role found for the authenticated user");
        }
        return role;
    }

    private static UserRoleName fromStringSafe(String name) {
        try {
            return UserRoleName.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
