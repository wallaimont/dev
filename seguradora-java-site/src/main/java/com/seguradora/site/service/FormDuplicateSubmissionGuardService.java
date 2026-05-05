package com.seguradora.site.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FormDuplicateSubmissionGuardService {

    private final FormValidationFeatureFlagService featureFlagService;
    private final Map<String, Long> tokenRegistry = new ConcurrentHashMap<>();

    public FormDuplicateSubmissionGuardService(FormValidationFeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isDuplicate(String formType, String submissionToken) {
        if (submissionToken == null || submissionToken.isBlank()) {
            return false;
        }

        purgeExpiredTokens();
        long now = Instant.now().toEpochMilli();
        long ttlMs = featureFlagService.duplicateWindowSeconds() * 1000;
        String key = formType + ":" + submissionToken;
        Long previous = tokenRegistry.putIfAbsent(key, now);

        if (previous == null) {
            return false;
        }

        if ((now - previous) <= ttlMs) {
            return true;
        }

        tokenRegistry.put(key, now);
        return false;
    }

    private void purgeExpiredTokens() {
        long now = Instant.now().toEpochMilli();
        long ttlMs = featureFlagService.duplicateWindowSeconds() * 1000;
        tokenRegistry.entrySet().removeIf(entry -> (now - entry.getValue()) > ttlMs);
    }
}
