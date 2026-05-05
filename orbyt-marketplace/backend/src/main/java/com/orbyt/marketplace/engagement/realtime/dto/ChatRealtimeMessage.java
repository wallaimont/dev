package com.orbyt.marketplace.engagement.realtime.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatRealtimeMessage(
        UUID chatId,
        UUID messageId,
        UUID senderId,
        String content,
        String messageType,
        OffsetDateTime sentAt,
        int buyerUnread,
        int sellerUnread
) {
}
