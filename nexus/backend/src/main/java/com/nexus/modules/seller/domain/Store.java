package com.nexus.modules.seller.domain;

import com.nexus.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stores",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "slug"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Store extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "seller_id", nullable = false, unique = true)
    private UUID sellerId;

    @Column(nullable = false, length = 150)
    private String slug;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Column(name = "total_sales")
    private Integer totalSales = 0;

    @Column(columnDefinition = "TEXT")
    private String policies;

    @Column(name = "return_policy", columnDefinition = "TEXT")
    private String returnPolicy;

    @Column(name = "social_instagram", length = 100)
    private String socialInstagram;

    @Column(name = "social_facebook", length = 100)
    private String socialFacebook;

    @Column(name = "social_website", length = 255)
    private String socialWebsite;
}
