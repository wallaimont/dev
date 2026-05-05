package com.orbyt.marketplace.engagement.domain;

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
@Table(name = "chats")
public class Chat extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID buyerId;

    @Column(nullable = false)
    private UUID sellerId;

    @Column
    private UUID orderId;

    @Column
    private String subject;

    @Column
    private OffsetDateTime lastMessageAt;

    @Column(nullable = false)
    private int buyerUnread = 0;

    @Column(nullable = false)
    private int sellerUnread = 0;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();
}
