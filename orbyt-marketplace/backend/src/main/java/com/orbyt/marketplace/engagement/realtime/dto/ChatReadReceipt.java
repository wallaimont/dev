package com.orbyt.marketplace.engagement.realtime.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatReadReceipt(
        UUID chatId,
        UUID readerId,
        int buyerUnread,
        int sellerUnread,
        OffsetDateTime readAt
) {
}
