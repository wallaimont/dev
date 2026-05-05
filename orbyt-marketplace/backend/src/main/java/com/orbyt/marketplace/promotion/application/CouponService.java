package com.orbyt.marketplace.promotion.application;

import com.orbyt.marketplace.promotion.domain.Coupon;
import com.orbyt.marketplace.promotion.domain.CouponUsage;
import com.orbyt.marketplace.promotion.repository.CouponRepository;
import com.orbyt.marketplace.promotion.repository.CouponUsageRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    public Coupon validateCoupon(String code, BigDecimal orderTotal) {
        UUID tenantId = TenantContext.require();
        Coupon coupon = couponRepository.findByTenantIdAndCodeIgnoreCase(tenantId, code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
        if (!coupon.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Coupon is expired or inactive");
        }
        if (coupon.getMinimumOrderValue() != null && orderTotal.compareTo(coupon.getMinimumOrderValue()) < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Minimum order value not reached: " + coupon.getMinimumOrderValue());
        }
        return coupon;
    }

    @Transactional
    public BigDecimal applyCoupon(String code, UUID userId, UUID orderId, BigDecimal orderTotal) {
        Coupon coupon = validateCoupon(code, orderTotal);
        if (coupon.getMaxUsagesPerUser() != null) {
            long userUsages = couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), userId);
            if (userUsages >= coupon.getMaxUsagesPerUser()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Coupon usage limit reached for this user");
            }
        }
        BigDecimal discount = coupon.calculateDiscount(orderTotal);

        CouponUsage usage = new CouponUsage();
        usage.setCouponId(coupon.getId());
        usage.setUserId(userId);
        usage.setOrderId(orderId);
        usage.setDiscountApplied(discount);
        couponUsageRepository.save(usage);

        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);

        return discount;
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        coupon.setTenantId(TenantContext.require());
        coupon.setUsageCount(0);
        return couponRepository.save(coupon);
    }
}
