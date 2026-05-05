package com.nexus.modules.payment.domain;

public enum PaymentStatus {
    PENDING, PROCESSING, PAID, FAILED, CANCELLED, REFUNDED, DISPUTED;
    public boolean isTerminal() {
        return this == PAID || this == FAILED || this == CANCELLED || this == REFUNDED;
    }
}
