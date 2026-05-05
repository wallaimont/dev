package com.nexus.modules.chat.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
public class ChatMessageRequest {
    private UUID recipientId;
    private String content;
}
