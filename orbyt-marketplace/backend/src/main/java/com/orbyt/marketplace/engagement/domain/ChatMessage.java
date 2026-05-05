package com.orbyt.marketplace.engagement.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends TenantScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false)
    private String messageType = "TEXT";

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String attachmentUrl;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column
    private OffsetDateTime readAt;
}
