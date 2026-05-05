package com.supportdesk.config;

import com.supportdesk.domain.entity.Role;
import com.supportdesk.domain.entity.User;
import com.supportdesk.repository.RoleRepository;
import com.supportdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapInitializer implements ApplicationRunner {

    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        AppProperties.BootstrapAdmin cfg = appProperties.getBootstrapAdmin();
        if (!cfg.isEnabled()) {
            log.info("Admin bootstrap desabilitado por configuracao.");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Role ADMIN nao encontrada. Verifique as migrations."));

        Optional<User> existing = userRepository.findAnyByEmail(cfg.getEmail());
        if (existing.isPresent()) {
            ensureExistingAdmin(existing.get(), adminRole, cfg);
            return;
        }

        User admin = User.builder()
                .name(cfg.getName())
                .email(cfg.getEmail())
                .passwordHash(passwordEncoder.encode(cfg.getPassword()))
                .enabled(true)
                .locked(false)
                .failedAttempts(0)
                .roles(new HashSet<>())
                .build();
        admin.getRoles().add(adminRole);

        userRepository.save(admin);
        log.warn("Usuario admin bootstrap criado automaticamente: {}", cfg.getEmail());
    }

    private void ensureExistingAdmin(User admin, Role adminRole, AppProperties.BootstrapAdmin cfg) {
        boolean changed = false;

        if (admin.getDeletedAt() != null) {
            admin.setDeletedAt(null);
            changed = true;
        }

        if (!admin.isEnabled()) {
            admin.setEnabled(true);
            changed = true;
        }

        if (admin.isLocked()) {
            admin.setLocked(false);
            changed = true;
        }

        if (admin.getFailedAttempts() != 0) {
            admin.setFailedAttempts(0);
            changed = true;
        }

        if (admin.getLockedUntil() != null) {
            admin.setLockedUntil(null);
            changed = true;
        }

        if (admin.getRoles() == null) {
            admin.setRoles(new HashSet<>());
            changed = true;
        }

        if (!admin.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
            admin.getRoles().add(adminRole);
            changed = true;
        }

        if (cfg.isForcePasswordResetOnStartup() && !passwordEncoder.matches(cfg.getPassword(), admin.getPasswordHash())) {
            admin.setPasswordHash(passwordEncoder.encode(cfg.getPassword()));
            changed = true;
        }

        if (changed) {
            admin.setUpdatedAt(Instant.now());
            userRepository.save(admin);
            log.warn("Usuario admin bootstrap atualizado automaticamente: {}", cfg.getEmail());
        }
    }
}
