package com.nexus.modules.order.domain;

public enum OrderGroupStatus {
    PENDING, CONFIRMED, PROCESSING,
    AWAITING_SHIPMENT, SHIPPED,
    DELIVERED, CANCELLED, RETURNED
}
