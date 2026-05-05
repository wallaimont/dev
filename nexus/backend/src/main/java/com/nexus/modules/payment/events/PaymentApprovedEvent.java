package com.nexus.modules.payment.events;

import com.nexus.shared.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentApprovedEvent extends DomainEvent {
    private final UUID orderId;

    public PaymentApprovedEvent(UUID tenantId, UUID paymentId, UUID orderId) {
        super(tenantId, "Payment", paymentId);
        this.orderId = orderId;
    }

    @Override public String eventType() { return "PaymentApproved"; }
}
