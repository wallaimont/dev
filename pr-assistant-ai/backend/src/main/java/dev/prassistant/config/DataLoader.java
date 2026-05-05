package dev.prassistant.config;

import dev.prassistant.domain.entity.User;
import dev.prassistant.domain.enums.Role;
import dev.prassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@prassistant.dev")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);

            User reviewer = User.builder()
                    .name("Reviewer")
                    .email("reviewer@prassistant.dev")
                    .passwordHash(passwordEncoder.encode("reviewer123"))
                    .role(Role.REVIEWER)
                    .build();
            userRepository.save(reviewer);

            log.info("Seed data loaded: 2 users created (admin / reviewer)");
        }
    }
}
