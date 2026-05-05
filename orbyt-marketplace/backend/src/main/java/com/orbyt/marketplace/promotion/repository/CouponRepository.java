package com.orbyt.marketplace.promotion.repository;

import com.orbyt.marketplace.promotion.domain.Coupon;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByTenantIdAndCodeIgnoreCase(UUID tenantId, String code);
}
