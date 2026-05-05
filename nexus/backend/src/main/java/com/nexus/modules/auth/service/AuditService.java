package com.nexus.modules.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AuditService {

    public void recordSuccessfulLogin(UUID userId, String ipAddress) {
        log.info("Successful login: userId={} ip={}", userId, ipAddress);
    }

    public void recordFailedLogin(UUID userId, String ipAddress) {
        log.warn("Failed login attempt: userId={} ip={}", userId, ipAddress);
    }
}
