package com.orbyt.marketplace.fraud.application;

import com.orbyt.marketplace.IntegrationTestBase;
import com.orbyt.marketplace.fraud.domain.FraudAnalysis;
import com.orbyt.marketplace.fraud.domain.FraudBlacklist;
import com.orbyt.marketplace.fraud.repository.FraudAnalysisRepository;
import com.orbyt.marketplace.fraud.repository.FraudBlacklistRepository;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FraudServiceIntegrationTest extends IntegrationTestBase {

    @Autowired FraudService fraudService;
    @Autowired FraudBlacklistRepository blacklistRepository;
    @Autowired FraudAnalysisRepository analysisRepository;
    @Autowired TenantRepository tenantRepository;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = tenantRepository.findBySlugIgnoreCase("orbyt-demo")
                .orElseThrow().getId();
        TenantContext.set(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldApproveCleanOrder() {
        UUID orderId = UUID.randomUUID();
        UUID buyerId = UUID.randomUUID();

        FraudAnalysis result = fraudService.analyzeOrder(
                orderId, buyerId, new BigDecimal("500.00"),
                "clean@example.com", "192.168.1.1", "fp-clean-123");

        assertThat(result).isNotNull();
        assertThat(result.getDecision()).isEqualTo(FraudAnalysis.Decision.APPROVED);
        assertThat(result.getRiskScore().compareTo(BigDecimal.valueOf(50))).isLessThan(0);
        assertThat(analysisRepository.findByOrderId(orderId)).isPresent();
    }

    @Test
    void shouldRejectBlacklistedEmail() {
        FraudBlacklist bl = new FraudBlacklist();
        bl.setTenantId(tenantId);
        bl.setType(FraudBlacklist.BlacklistType.EMAIL);
        bl.setValue("fraud@evil.com");
        bl.setReason("Known fraud");
        bl.setActive(true);
        blacklistRepository.save(bl);

        UUID orderId = UUID.randomUUID();
        UUID buyerId = UUID.randomUUID();

        FraudAnalysis result = fraudService.analyzeOrder(
                orderId, buyerId, new BigDecimal("500.00"),
                "fraud@evil.com", "10.0.0.1", null);

        assertThat(result.getDecision()).isEqualTo(FraudAnalysis.Decision.REJECTED);
        assertThat(result.getRiskScore().compareTo(BigDecimal.valueOf(80))).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldSetReviewForBlacklistedIP() {
        FraudBlacklist bl = new FraudBlacklist();
        bl.setTenantId(tenantId);
        bl.setType(FraudBlacklist.BlacklistType.IP_ADDRESS);
        bl.setValue("203.0.113.42");
        bl.setReason("Suspicious IP");
        bl.setActive(true);
        blacklistRepository.save(bl);

        FraudAnalysis result = fraudService.analyzeOrder(
                UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("300.00"),
                "normal@user.com", "203.0.113.42", null);

        // IP adds 40 points => APPROVED (<50)
        assertThat(result.getDecision()).isEqualTo(FraudAnalysis.Decision.APPROVED);
        assertThat(result.getRiskScore().compareTo(BigDecimal.valueOf(30))).isGreaterThan(0);
    }

    @Test
    void shouldSetReviewWhenRiskScoreBetween50And79() {
        FraudBlacklist ip = new FraudBlacklist();
        ip.setTenantId(tenantId);
        ip.setType(FraudBlacklist.BlacklistType.IP_ADDRESS);
        ip.setValue("198.51.100.10");
        ip.setReason("Suspicious region");
        ip.setActive(true);
        blacklistRepository.save(ip);

        FraudBlacklist fingerprint = new FraudBlacklist();
        fingerprint.setTenantId(tenantId);
        fingerprint.setType(FraudBlacklist.BlacklistType.DEVICE_FINGERPRINT);
        fingerprint.setValue("fp-review-001");
        fingerprint.setReason("Risky device");
        fingerprint.setActive(true);
        blacklistRepository.save(fingerprint);

        // IP(40) + highValue(20) = 60 => REVIEW
        FraudAnalysis result = fraudService.analyzeOrder(
                UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("12000.00"),
                "safe@user.com", "198.51.100.10", null);

        assertThat(result.getDecision()).isEqualTo(FraudAnalysis.Decision.REVIEW);
        assertThat(result.getRiskScore()).isEqualByComparingTo("60");
    }

    @Test
    void shouldFlagHighValueOrderWithBlacklistedDevice() {
        FraudBlacklist bl = new FraudBlacklist();
        bl.setTenantId(tenantId);
        bl.setType(FraudBlacklist.BlacklistType.DEVICE_FINGERPRINT);
        bl.setValue("fp-suspect-999");
        bl.setReason("Stolen device");
        bl.setActive(true);
        blacklistRepository.save(bl);

        // Device(60) + highValue(20) = 80 => REJECTED
        FraudAnalysis result = fraudService.analyzeOrder(
                UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("15000.00"),
                "buyer@store.com", "192.168.1.100", "fp-suspect-999");

        assertThat(result.getDecision()).isEqualTo(FraudAnalysis.Decision.REJECTED);
    }

    @Test
    void shouldAddToBlacklistAndUseInAnalysis() {
        fraudService.addToBlacklist(FraudBlacklist.BlacklistType.EMAIL, "new.fraud@test.com", "New fraud entry");

        FraudAnalysis result = fraudService.analyzeOrder(
                UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"),
                "new.fraud@test.com", "10.10.10.10", null);

        assertThat(result.getDecision()).isEqualTo(FraudAnalysis.Decision.REJECTED);
    }
}
