package com.supportdesk.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String friendlyMessage;
    private String technicalMessage;
    private String correlationId;
    private List<FieldError> errors;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private String path;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
