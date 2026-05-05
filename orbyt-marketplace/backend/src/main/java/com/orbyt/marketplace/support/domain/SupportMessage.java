package com.orbyt.marketplace.support.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "support_messages")
public class SupportMessage extends TenantScopedEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Column(nullable = false)
    private java.util.UUID senderId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column
    private String attachmentUrl;

    @Column(nullable = false)
    private boolean isInternal = false;
}
