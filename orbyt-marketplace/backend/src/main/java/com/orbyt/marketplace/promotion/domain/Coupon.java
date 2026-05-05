package com.orbyt.marketplace.promotion.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "coupons")
@Getter @Setter @NoArgsConstructor
public class Coupon extends TenantScopedEntity {

    @Column(nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 12, scale = 2)
    private BigDecimal minimumOrderValue;

    @Column(precision = 12, scale = 2)
    private BigDecimal maximumDiscount;

    private Integer maxUsages;
    private Integer usageCount = 0;
    private Integer maxUsagesPerUser;

    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;

    private boolean active = true;

    @Column(length = 500)
    private String description;

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
    }

    public boolean isValid() {
        if (!active) return false;
        LocalDateTime now = LocalDateTime.now();
        if (startsAt != null && now.isBefore(startsAt)) return false;
        if (expiresAt != null && now.isAfter(expiresAt)) return false;
        if (maxUsages != null && usageCount >= maxUsages) return false;
        return true;
    }

    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (minimumOrderValue != null && orderTotal.compareTo(minimumOrderValue) < 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = orderTotal.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            discount = discountValue;
        } else {
            return BigDecimal.ZERO;
        }
        if (maximumDiscount != null && discount.compareTo(maximumDiscount) > 0) {
            discount = maximumDiscount;
        }
        return discount;
    }
}
