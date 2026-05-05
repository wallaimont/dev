package com.nexus.modules.order.events;

import com.nexus.shared.events.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderCreatedEvent extends DomainEvent {
    private final UUID buyerId;
    private final BigDecimal total;

    public OrderCreatedEvent(UUID tenantId, UUID orderId, UUID buyerId, BigDecimal total) {
        super(tenantId, "Order", orderId);
        this.buyerId = buyerId;
        this.total = total;
    }

    @Override public String eventType() { return "OrderCreated"; }
}
