package com.nexus.shared.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events.
 * Each module publishes typed events via OutboxEventPublisher.
 * Events are persisted in outbox_events table before being sent to Kafka.
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class DomainEvent {

    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final UUID tenantId;
    private final String aggregateType;
    private final UUID aggregateId;

    protected DomainEvent(UUID tenantId, String aggregateType, UUID aggregateId) {
        this.tenantId = tenantId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }

    public abstract String eventType();
}
