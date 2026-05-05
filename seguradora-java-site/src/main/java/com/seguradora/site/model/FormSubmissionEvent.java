package com.seguradora.site.model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormSubmissionEvent {

    private final String submissionId;
    private final String formType;
    private final String status;
    private final Map<String, List<String>> fieldErrors;
    private final long durationMs;
    private final Instant occurredAt;
    private final int httpStatus;

    public FormSubmissionEvent(
            String submissionId,
            String formType,
            String status,
            Map<String, List<String>> fieldErrors,
            long durationMs,
            Instant occurredAt,
            int httpStatus
    ) {
        this.submissionId = submissionId;
        this.formType = formType;
        this.status = status;
        this.fieldErrors = fieldErrors == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(fieldErrors));
        this.durationMs = durationMs;
        this.occurredAt = occurredAt;
        this.httpStatus = httpStatus;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public String getFormType() {
        return formType;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
