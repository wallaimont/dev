package com.supportdesk.domain.entity;

import com.supportdesk.domain.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_from", columnDefinition = "ticket_status")
    private TicketStatus statusFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_to", columnDefinition = "ticket_status")
    private TicketStatus statusTo;

    @Column(name = "assignee_from")
    private UUID assigneeFrom;

    @Column(name = "assignee_to")
    private UUID assigneeTo;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    @Column(length = 500)
    private String reason;

    @Column(name = "changed_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant changedAt = Instant.now();
}
