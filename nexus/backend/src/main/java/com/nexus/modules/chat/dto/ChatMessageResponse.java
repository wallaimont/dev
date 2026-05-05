package com.nexus.modules.chat.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data @Builder
public class ChatMessageResponse {
    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private String content;
    private Instant sentAt;
}
