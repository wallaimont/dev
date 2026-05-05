package com.insuranceflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.insuranceflow.security.UserPrincipal;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return Optional.of("SYSTEM");
            }
            if (auth.getPrincipal() instanceof UserPrincipal principal) {
                return Optional.of(principal.getEmail());
            }
            return Optional.of("SYSTEM");
        };
    }
}
