package com.nexus.modules.seller.domain;

import com.nexus.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sellers",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seller extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SellerStatus status = SellerStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type", nullable = false, length = 20)
    private SellerType sellerType = SellerType.PF;

    @Column(nullable = false, length = 18)
    private String document;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "trade_name", length = 255)
    private String tradeName;

    @Column(name = "commission_rate", precision = 5, scale = 4)
    private BigDecimal commissionRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SellerLevel level = SellerLevel.BRONZE;

    @Column(name = "is_premium", nullable = false)
    private boolean isPremium = false;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Convenience
    public boolean isActive() { return status == SellerStatus.ACTIVE; }

    public enum SellerStatus { PENDING, ACTIVE, SUSPENDED, BANNED, REJECTED, UNDER_REVIEW }
    public enum SellerType   { PF, PJ }
    public enum SellerLevel  { BRONZE, SILVER, GOLD, PLATINUM, DIAMOND }
}
