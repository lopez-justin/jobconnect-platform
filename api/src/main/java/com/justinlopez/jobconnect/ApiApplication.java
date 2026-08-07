package com.justinlopez.jobconnect;

import com.justinlopez.jobconnect.domain.model.Role;
import com.justinlopez.jobconnect.domain.model.User;
import com.justinlopez.jobconnect.domain.model.enums.UserRoleName;
import com.justinlopez.jobconnect.domain.model.vo.Email;
import com.justinlopez.jobconnect.domain.repository.RoleRepository;
import com.justinlopez.jobconnect.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class ApiApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiApplication.class);

    static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initAdminUser(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return _ -> {
            log.info("Checking if admin user exists: {}", adminEmail);

            if (!userRepository.existsByEmailIgnoreCase(adminEmail)) {
                Role adminRole = roleRepository.findByName(UserRoleName.ADMIN)
                        .orElseThrow(() -> new IllegalStateException("Role 'ADMIN' not found in database."));

                User adminUser = new User(
                        null,
                        new Email(adminEmail),
                        "System Administrator",
                        passwordEncoder.encode(adminPassword),
                        "0000000000",
                        true,
                        Set.of(adminRole)
                );
                userRepository.save(adminUser);
                log.info("Admin user created successfully! Email: {}", adminEmail);
            } else {
                log.info("Admin user already exists: {}", adminEmail);
            }

        };
    }

}
