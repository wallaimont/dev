package com.orbyt.marketplace.promotion.domain;

import com.orbyt.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "coupon_usages")
@Getter @Setter @NoArgsConstructor
public class CouponUsage extends BaseEntity {

    @Column(nullable = false)
    private UUID couponId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountApplied;
}
