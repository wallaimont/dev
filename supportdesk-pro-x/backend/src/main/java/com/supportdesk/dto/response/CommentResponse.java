package com.supportdesk.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private String content;
    private boolean internal;
    private UserSummaryResponse author;
    private Instant createdAt;
    private Instant updatedAt;
}
