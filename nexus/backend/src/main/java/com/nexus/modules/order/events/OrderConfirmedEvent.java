package com.nexus.modules.order.events;

import com.nexus.shared.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderConfirmedEvent extends DomainEvent {
    private final UUID buyerId;

    public OrderConfirmedEvent(UUID tenantId, UUID orderId, UUID buyerId) {
        super(tenantId, "Order", orderId);
        this.buyerId = buyerId;
    }

    @Override public String eventType() { return "OrderConfirmed"; }
}
