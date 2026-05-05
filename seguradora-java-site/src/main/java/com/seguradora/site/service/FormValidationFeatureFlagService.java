package com.seguradora.site.service;

import com.seguradora.site.config.FormValidationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FormValidationFeatureFlagService {

    private static final Logger log = LoggerFactory.getLogger(FormValidationFeatureFlagService.class);

    private final FormValidationProperties properties;

    public FormValidationFeatureFlagService(FormValidationProperties properties) {
        this.properties = properties;
    }

    public boolean isEnhancedValidationEnabled(String formType) {
        try {
            return properties.isEnhancedEnabled();
        } catch (RuntimeException ex) {
            log.warn("Falha ao avaliar feature flag de validacao para formType={} fallbackToLegacy={} erro={}",
                    formType,
                    properties.isFallbackToLegacyOnError(),
                    ex.getMessage());
            return !properties.isFallbackToLegacyOnError();
        }
    }

    public long duplicateWindowSeconds() {
        return Math.max(1, properties.getDuplicateWindowSeconds());
    }

    public boolean isTelemetryEnabled() {
        return properties.isTelemetryEnabled();
    }
}
