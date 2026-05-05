package com.orbyt.marketplace.promotion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyt.marketplace.IntegrationTestBase;
import com.orbyt.marketplace.promotion.domain.Coupon;
import com.orbyt.marketplace.promotion.repository.CouponRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired CouponRepository couponRepository;
    @Autowired TenantRepository tenantRepository;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = tenantRepository.findBySlugIgnoreCase("orbyt-demo")
                .orElseThrow().getId();
    }

    private String loginAdmin() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "email", "admin@orbyt.local",
                "password", "Admin@123"
        ));
        String response = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private void createTestCoupon(String code, Coupon.DiscountType type, BigDecimal value) {
        Coupon coupon = new Coupon();
        coupon.setTenantId(tenantId);
        coupon.setCode(code);
        coupon.setDiscountType(type);
        coupon.setDiscountValue(value);
        coupon.setActive(true);
        coupon.setStartsAt(LocalDateTime.now().minusDays(1));
        coupon.setExpiresAt(LocalDateTime.now().plusDays(30));
        coupon.setStatus("ACTIVE");
        couponRepository.save(coupon);
    }

    @Test
    void shouldValidatePercentageCoupon() throws Exception {
        createTestCoupon("SAVE10", Coupon.DiscountType.PERCENTAGE, BigDecimal.TEN);

        mockMvc.perform(get("/v1/coupons/validate")
                        .param("code", "SAVE10")
                        .param("orderTotal", "200.00")
                        .header("X-Tenant-Id", "orbyt-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.discountType").value("PERCENTAGE"))
                .andExpect(jsonPath("$.discountAmount").value(20.0));
    }

    @Test
    void shouldValidateFixedAmountCoupon() throws Exception {
        createTestCoupon("FLAT50", Coupon.DiscountType.FIXED_AMOUNT, new BigDecimal("50.00"));

        mockMvc.perform(get("/v1/coupons/validate")
                        .param("code", "FLAT50")
                        .param("orderTotal", "300.00")
                        .header("X-Tenant-Id", "orbyt-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("FLAT50"))
                .andExpect(jsonPath("$.discountType").value("FIXED_AMOUNT"))
                .andExpect(jsonPath("$.discountAmount").value(50.0));
    }

    @Test
    void shouldReturnNotFoundForInvalidCoupon() throws Exception {
        mockMvc.perform(get("/v1/coupons/validate")
                        .param("code", "DOESNOTEXIST")
                        .param("orderTotal", "100.00")
                        .header("X-Tenant-Id", "orbyt-demo"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectExpiredCoupon() throws Exception {
        Coupon expired = new Coupon();
        expired.setTenantId(tenantId);
        expired.setCode("EXPIRED99");
        expired.setDiscountType(Coupon.DiscountType.PERCENTAGE);
        expired.setDiscountValue(BigDecimal.TEN);
        expired.setActive(true);
        expired.setStartsAt(LocalDateTime.now().minusDays(30));
        expired.setExpiresAt(LocalDateTime.now().minusDays(1));
        expired.setStatus("ACTIVE");
        couponRepository.save(expired);

        mockMvc.perform(get("/v1/coupons/validate")
                        .param("code", "EXPIRED99")
                        .param("orderTotal", "100.00")
                        .header("X-Tenant-Id", "orbyt-demo"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldCreateCouponAsAdmin() throws Exception {
        String token = loginAdmin();

        String couponPayload = objectMapper.writeValueAsString(Map.of(
                "code", "NEWCOUPON",
                "discountType", "PERCENTAGE",
                "discountValue", 15,
                "description", "Teste de criação",
                "active", true
        ));

        mockMvc.perform(post("/v1/coupons")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(couponPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("NEWCOUPON"))
                .andExpect(jsonPath("$.discountType").value("PERCENTAGE"));
    }

    @Test
    void shouldForbidCouponCreationForBuyer() throws Exception {
        String registerPayload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "fullName", "Buyer Coupon",
                "email", "buyer.coupon@orbyt.local",
                "password", "Buyer@123"
        ));
        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isOk());

        String loginPayload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "email", "buyer.coupon@orbyt.local",
                "password", "Buyer@123"
        ));
        String response = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String buyerToken = objectMapper.readTree(response).get("accessToken").asText();

        String couponPayload = objectMapper.writeValueAsString(Map.of(
                "code", "FORBIDDEN",
                "discountType", "PERCENTAGE",
                "discountValue", 10
        ));

        mockMvc.perform(post("/v1/coupons")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(couponPayload))
                .andExpect(status().isForbidden());
    }

        @Test
        void shouldRequireAuthenticationForCouponCreation() throws Exception {
                String couponPayload = objectMapper.writeValueAsString(Map.of(
                                "code", "NEEDAUTH",
                                "discountType", "PERCENTAGE",
                                "discountValue", 10
                ));

                mockMvc.perform(post("/v1/coupons")
                                                .header("X-Tenant-Id", "orbyt-demo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(couponPayload))
                                .andExpect(status().isUnauthorized());
        }
}
