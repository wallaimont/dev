package com.orbyt.marketplace.order.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String fromStatus;

    @Column(nullable = false)
    private String toStatus;

    @Column
    private String reason;

    @Column
    private UUID changedBy;

    @Column(nullable = false)
    private OffsetDateTime changedAt = OffsetDateTime.now();
}
