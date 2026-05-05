package com.nexus.modules.order.events;

import com.nexus.shared.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderShippedEvent extends DomainEvent {
    private final UUID groupId;
    private final String trackingCode;

    public OrderShippedEvent(UUID tenantId, UUID orderId, UUID groupId, String trackingCode) {
        super(tenantId, "Order", orderId);
        this.groupId = groupId;
        this.trackingCode = trackingCode;
    }

    @Override public String eventType() { return "OrderShipped"; }
}
