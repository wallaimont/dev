package com.orbyt.marketplace.engagement.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "reviews")
public class Review extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID productId;

    @Column
    private UUID orderId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int rating;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column
    private String[] images;

    @Column(nullable = false)
    private boolean verifiedPurchase = false;

    @Column(nullable = false)
    private int helpfulCount = 0;

    @Column(columnDefinition = "TEXT")
    private String reply;

    @Column
    private UUID repliedBy;

    @Column
    private OffsetDateTime repliedAt;
}
