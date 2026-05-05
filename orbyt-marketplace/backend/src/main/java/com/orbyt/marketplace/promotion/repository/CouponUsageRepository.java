package com.orbyt.marketplace.promotion.repository;

import com.orbyt.marketplace.promotion.domain.CouponUsage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {

    long countByCouponIdAndUserId(UUID couponId, UUID userId);
}
