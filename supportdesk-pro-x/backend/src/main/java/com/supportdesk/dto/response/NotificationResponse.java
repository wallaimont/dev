package com.supportdesk.dto.response;

import com.supportdesk.domain.enums.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String body;
    private NotificationType type;
    private NotificationStatus status;
    private UUID referenceId;
    private String referenceType;
    private Instant createdAt;
    private Instant readAt;
}
