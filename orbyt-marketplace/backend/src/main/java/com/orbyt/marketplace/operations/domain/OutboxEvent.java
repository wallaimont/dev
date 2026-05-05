package com.orbyt.marketplace.operations.domain;

import com.orbyt.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "outbox_events")
public class OutboxEvent extends BaseEntity {

    @Column
    private UUID tenantId;

    @Column(nullable = false)
    private String aggregateType;

    @Column
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean published = false;

    @Column
    private OffsetDateTime publishedAt;

    @Column(nullable = false)
    private int retryCount = 0;

    @Column
    private OffsetDateTime nextRetryAt;

    @Column(nullable = false)
    private String status = "PENDING";
}
