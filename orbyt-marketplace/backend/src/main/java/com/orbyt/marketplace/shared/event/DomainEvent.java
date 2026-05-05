package com.orbyt.marketplace.shared.event;

import java.util.Map;
import java.util.UUID;

public record DomainEvent(
    String eventType,
    String aggregateType,
    UUID aggregateId,
    Map<String, Object> payload
) {
    public String idempotencyKey() {
        return aggregateType + ":" + aggregateId + ":" + eventType + ":" + System.nanoTime();
    }
}
