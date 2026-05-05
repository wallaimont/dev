package com.orbyt.marketplace.engagement.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "notifications")
public class Notification extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String notificationType;

    @Column(nullable = false)
    private String channel = "INTERNAL";

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String data;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column
    private OffsetDateTime readAt;

    @Column
    private OffsetDateTime sentAt;

    @Column(nullable = false)
    private String deliveryStatus = "PENDING";
}
