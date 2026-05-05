package com.seguradora.site.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.form-validation")
public class FormValidationProperties {

    private boolean enhancedEnabled = true;
    private boolean fallbackToLegacyOnError = true;
    private boolean telemetryEnabled = true;
    private long duplicateWindowSeconds = 20;

    public boolean isEnhancedEnabled() {
        return enhancedEnabled;
    }

    public void setEnhancedEnabled(boolean enhancedEnabled) {
        this.enhancedEnabled = enhancedEnabled;
    }

    public boolean isFallbackToLegacyOnError() {
        return fallbackToLegacyOnError;
    }

    public void setFallbackToLegacyOnError(boolean fallbackToLegacyOnError) {
        this.fallbackToLegacyOnError = fallbackToLegacyOnError;
    }

    public boolean isTelemetryEnabled() {
        return telemetryEnabled;
    }

    public void setTelemetryEnabled(boolean telemetryEnabled) {
        this.telemetryEnabled = telemetryEnabled;
    }

    public long getDuplicateWindowSeconds() {
        return duplicateWindowSeconds;
    }

    public void setDuplicateWindowSeconds(long duplicateWindowSeconds) {
        this.duplicateWindowSeconds = duplicateWindowSeconds;
    }
}
