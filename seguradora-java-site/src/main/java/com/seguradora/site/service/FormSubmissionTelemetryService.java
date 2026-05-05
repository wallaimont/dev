package com.seguradora.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.site.model.FormSubmissionEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class FormSubmissionTelemetryService {

    private static final Logger log = LoggerFactory.getLogger(FormSubmissionTelemetryService.class);

    private final ObjectMapper objectMapper;
    private final FormValidationFeatureFlagService featureFlagService;
    private final MeterRegistry meterRegistry;

    public FormSubmissionTelemetryService(
            ObjectMapper objectMapper,
            FormValidationFeatureFlagService featureFlagService,
            @Nullable MeterRegistry meterRegistry
    ) {
        this.objectMapper = objectMapper;
        this.featureFlagService = featureFlagService;
        this.meterRegistry = meterRegistry;
    }

    public void success(String submissionId, String formType, long durationMs) {
        record(submissionId, formType, "SUCCESS", Collections.emptyMap(), durationMs, 200);
    }

    public void validationError(String submissionId, String formType, Map<String, List<String>> fieldErrors, long durationMs) {
        record(submissionId, formType, "VALIDATION_ERROR", fieldErrors, durationMs, 200);
    }

    public void internalError(String submissionId, String formType, long durationMs) {
        record(submissionId, formType, "INTERNAL_ERROR", Collections.emptyMap(), durationMs, 200);
    }

    private void record(
            String submissionId,
            String formType,
            String status,
            Map<String, List<String>> fieldErrors,
            long durationMs,
            int httpStatus
    ) {
        if (!featureFlagService.isTelemetryEnabled()) {
            return;
        }

        FormSubmissionEvent event = new FormSubmissionEvent(
                submissionId,
                formType,
                status,
                fieldErrors,
                durationMs,
                Instant.now(),
                httpStatus
        );

        log.info("form_submission_event={} ", toJson(event));
        if (meterRegistry != null) {
            Counter.builder("form_submission_total")
                    .tag("formType", formType)
                    .tag("status", status)
                    .register(meterRegistry)
                    .increment();

            Timer.builder("form_submission_latency_ms")
                    .tag("formType", formType)
                    .register(meterRegistry)
                    .record(durationMs, TimeUnit.MILLISECONDS);
        }
    }

    private String toJson(FormSubmissionEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            log.warn("Falha ao serializar evento de submissao id={} erro={}", event.getSubmissionId(), ex.getMessage());
            return "{\"submissionId\":\"" + event.getSubmissionId() + "\",\"status\":\"" + event.getStatus() + "\"}";
        }
    }
}
