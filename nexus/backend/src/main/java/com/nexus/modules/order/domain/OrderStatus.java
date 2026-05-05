package com.nexus.modules.order.domain;

public enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING,
    PARTIALLY_SHIPPED, SHIPPED,
    DELIVERED, CANCELLED, REFUNDING, REFUNDED;

    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isRefundable() {
        return this == DELIVERED || this == PARTIALLY_SHIPPED;
    }
}
