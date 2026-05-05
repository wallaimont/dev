package com.supportdesk.domain.entity;

import com.supportdesk.domain.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "event_outbox")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 150)
    private String eventType;

    @Column(nullable = false, columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> payload;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "outbox_status")
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "kafka_topic", nullable = false, length = 200)
    private String kafkaTopic;

    @Column(nullable = false)
    @Builder.Default
    private short attempts = 0;

    @Column(name = "max_attempts", nullable = false)
    @Builder.Default
    private short maxAttempts = 3;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "next_retry_at", nullable = false)
    @Builder.Default
    private Instant nextRetryAt = Instant.now();
}
