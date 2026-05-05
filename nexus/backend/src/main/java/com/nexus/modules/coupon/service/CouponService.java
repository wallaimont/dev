package com.nexus.modules.coupon.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class CouponService {

    public BigDecimal applyCoupon(UUID cartId, String couponCode) {
        log.info("Applying coupon {} to cart {}", couponCode, cartId);
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateDiscount(String code, BigDecimal subtotal, UUID tenantId, UUID userId) {
        log.info("Calculating discount for coupon {} subtotal {}", code, subtotal);
        return BigDecimal.ZERO;
    }

    public void removeCoupon(UUID cartId) {
        log.info("Removing coupon from cart {}", cartId);
    }
}
