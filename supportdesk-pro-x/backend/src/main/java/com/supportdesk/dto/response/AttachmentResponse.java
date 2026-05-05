package com.supportdesk.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttachmentResponse {
    private UUID id;
    private String fileName;
    private String mimeType;
    private long sizeBytes;
    private String downloadUrl;
    private Instant uploadedAt;
    private UserSummaryResponse uploadedBy;
}
