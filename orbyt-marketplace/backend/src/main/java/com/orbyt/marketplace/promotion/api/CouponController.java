package com.orbyt.marketplace.promotion.api;

import com.orbyt.marketplace.promotion.application.CouponService;
import com.orbyt.marketplace.promotion.domain.Coupon;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/validate")
    public CouponValidationResponse validate(@RequestParam String code,
                                              @RequestParam BigDecimal orderTotal) {
        Coupon coupon = couponService.validateCoupon(code, orderTotal);
        BigDecimal discount = coupon.calculateDiscount(orderTotal);
        return new CouponValidationResponse(coupon.getCode(), coupon.getDiscountType().name(),
                discount, coupon.getDescription());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin.promotions.manage')")
    @ResponseStatus(HttpStatus.CREATED)
    public Coupon create(@RequestBody Coupon coupon) {
        return couponService.createCoupon(coupon);
    }

    public record CouponValidationResponse(String code, String discountType,
                                            BigDecimal discountAmount, String description) {}
}
