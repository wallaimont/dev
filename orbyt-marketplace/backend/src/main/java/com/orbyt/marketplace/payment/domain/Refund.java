package com.orbyt.marketplace.payment.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "refunds")
public class Refund extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID paymentId;

    @Column
    private UUID orderId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String refundMethod;

    @Column
    private OffsetDateTime processedAt;

    @Column
    private UUID processedBy;
}
