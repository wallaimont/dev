package com.nexus.modules.order.events;

import com.nexus.shared.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderCancelledEvent extends DomainEvent {
    private final String reason;

    public OrderCancelledEvent(UUID tenantId, UUID orderId, String reason) {
        super(tenantId, "Order", orderId);
        this.reason = reason;
    }

    @Override public String eventType() { return "OrderCancelled"; }
}
