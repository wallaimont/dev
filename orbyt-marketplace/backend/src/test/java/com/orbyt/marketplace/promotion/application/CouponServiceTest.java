package com.orbyt.marketplace.promotion.application;

import com.orbyt.marketplace.promotion.domain.Coupon;
import com.orbyt.marketplace.promotion.repository.CouponRepository;
import com.orbyt.marketplace.promotion.repository.CouponUsageRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponUsageRepository couponUsageRepository;

    @InjectMocks
    private CouponService couponService;

    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.set(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldApplyPercentageCouponAndIncrementUsage() {
        Coupon coupon = new Coupon();
        coupon.setId(UUID.randomUUID());
        coupon.setTenantId(tenantId);
        coupon.setCode("ORBYT10");
        coupon.setDiscountType(Coupon.DiscountType.PERCENTAGE);
        coupon.setDiscountValue(new BigDecimal("10"));
        coupon.setActive(true);
        coupon.setStartsAt(LocalDateTime.now().minusDays(1));
        coupon.setExpiresAt(LocalDateTime.now().plusDays(1));
        coupon.setUsageCount(0);
        coupon.setMaxUsagesPerUser(3);

        when(couponRepository.findByTenantIdAndCodeIgnoreCase(tenantId, "ORBYT10"))
                .thenReturn(Optional.of(coupon));
        when(couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), UUID.fromString("00000000-0000-0000-0000-000000000001")))
                .thenReturn(0L);

        BigDecimal discount = couponService.applyCoupon(
                "ORBYT10",
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                UUID.randomUUID(),
                new BigDecimal("200.00")
        );

        assertThat(discount).isEqualByComparingTo("20.00");
        assertThat(coupon.getUsageCount()).isEqualTo(1);
        verify(couponUsageRepository).save(any());
        verify(couponRepository).save(coupon);
    }

    @Test
    void shouldRejectExpiredCoupon() {
        Coupon coupon = new Coupon();
        coupon.setTenantId(tenantId);
        coupon.setCode("EXPIRED");
        coupon.setDiscountType(Coupon.DiscountType.FIXED_AMOUNT);
        coupon.setDiscountValue(new BigDecimal("50"));
        coupon.setActive(true);
        coupon.setStartsAt(LocalDateTime.now().minusDays(10));
        coupon.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(couponRepository.findByTenantIdAndCodeIgnoreCase(tenantId, "EXPIRED"))
                .thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> couponService.validateCoupon("EXPIRED", new BigDecimal("300.00")))
                .isInstanceOf(ResponseStatusException.class);
    }
}
