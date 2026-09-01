package com.justinlopez.jobconnect.infrastructure.security;

import com.justinlopez.jobconnect.domain.model.Role;
import com.justinlopez.jobconnect.domain.model.User;
import com.justinlopez.jobconnect.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        if (!user.isActive()) {
            log.warn("User is deactivated: {}", email);
            throw new UsernameNotFoundException("User account is deactivated");
        }

        return UserWithId.fromUser(user);
    }


    /**
     * A custom user details class that includes the user's ID.
     */
    public static class UserWithId extends org.springframework.security.core.userdetails.User {
        private final UUID userId;

        public UserWithId(String username, String password, Collection<? extends GrantedAuthority> authorities, UUID userId) {
            super(username, password, authorities);
            this.userId = userId;
        }

        public UUID getUserId() {
            return userId;
        }

        public static UserWithId fromUser(User user) {
            var authorities = user.getRoles().stream()
                    .map(Role::getName)
                    .map(roleName -> "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new UserWithId(
                    user.getEmail().value(),
                    user.getPasswordHash(),
                    authorities,
                    user.getId()
            );
        }
    }

}
