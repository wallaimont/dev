package com.orbyt.marketplace.support.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "disputes")
public class Dispute extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID buyerId;

    @Column(nullable = false)
    private UUID sellerId;

    @Column(nullable = false)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column
    private UUID resolvedBy;

    @Column
    private OffsetDateTime resolvedAt;

    @Column
    private BigDecimal refundAmount;
}
