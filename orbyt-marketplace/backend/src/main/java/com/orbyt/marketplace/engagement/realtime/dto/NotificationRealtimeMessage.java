package com.orbyt.marketplace.engagement.realtime.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationRealtimeMessage(
        UUID notificationId,
        String type,
        String title,
        String body,
        boolean read,
        OffsetDateTime createdAt,
        long unreadCount
) {
}
