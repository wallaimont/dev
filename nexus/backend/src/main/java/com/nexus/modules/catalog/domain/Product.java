package com.nexus.modules.catalog.domain;

import com.nexus.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "products",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "slug"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "brand_id")
    private UUID brandId;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false, length = 500)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(length = 20)
    private String condition = "NEW";

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "promotional_price", precision = 12, scale = 2)
    private BigDecimal promotionalPrice;

    @Column(name = "promo_starts_at")
    private Instant promoStartsAt;

    @Column(name = "promo_ends_at")
    private Instant promoEndsAt;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "BRL";

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "width_cm", precision = 8, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "height_cm", precision = 8, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "length_cm", precision = 8, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "total_sold")
    private Integer totalSold = 0;

    @Column(name = "is_digital", nullable = false)
    private boolean isDigital = false;

    @Column(name = "is_featured", nullable = false)
    private boolean isFeatured = false;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // Relationships
    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    // Convenience methods
    public BigDecimal getEffectivePrice() {
        if (promotionalPrice != null && isPromoActive()) return promotionalPrice;
        return basePrice;
    }

    public boolean isPromoActive() {
        Instant now = Instant.now();
        if (promotionalPrice == null) return false;
        if (promoStartsAt != null && now.isBefore(promoStartsAt)) return false;
        if (promoEndsAt != null && now.isAfter(promoEndsAt)) return false;
        return true;
    }

    public boolean isVisible() { return status == ProductStatus.ACTIVE && deletedAt == null; }

    public enum ProductStatus {
        DRAFT, PENDING_REVIEW, ACTIVE, INACTIVE, REJECTED, SUSPENDED
    }
}
