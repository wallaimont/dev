package com.nexus.config;

import com.nexus.shared.security.NexusPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class AuditingConfig {

    @Bean(name = "auditorProvider")
    public AuditorAware<UUID> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .filter(NexusPrincipal.class::isInstance)
            .map(NexusPrincipal.class::cast)
            .map(NexusPrincipal::getUserId);
    }
}