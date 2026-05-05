package com.orbyt.marketplace.support.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "support_tickets")
public class SupportTicket extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column
    private UUID orderId;

    @Column
    private String category;

    @Column(nullable = false)
    private String priority = "MEDIUM";

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private UUID assignedTo;

    @Column
    private OffsetDateTime resolvedAt;

    @Column
    private OffsetDateTime slaDeadline;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportMessage> messages = new ArrayList<>();
}
