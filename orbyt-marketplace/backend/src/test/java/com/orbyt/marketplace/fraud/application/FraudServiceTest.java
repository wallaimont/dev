package com.orbyt.marketplace.fraud.application;

import com.orbyt.marketplace.fraud.domain.FraudAnalysis;
import com.orbyt.marketplace.fraud.domain.FraudBlacklist;
import com.orbyt.marketplace.fraud.repository.FraudAnalysisRepository;
import com.orbyt.marketplace.fraud.repository.FraudBlacklistRepository;
import com.orbyt.marketplace.observability.MarketplaceMetrics;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudServiceTest {

    @Mock
    private FraudAnalysisRepository analysisRepository;

    @Mock
    private FraudBlacklistRepository blacklistRepository;

    @Mock
    private MarketplaceMetrics marketplaceMetrics;

    @InjectMocks
    private FraudService fraudService;

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
    void shouldMarkRejectedWhenEmailIsBlacklisted() {
        UUID orderId = UUID.randomUUID();
        UUID buyerId = UUID.randomUUID();

        when(blacklistRepository.existsByTenantIdAndTypeAndValueAndActiveTrue(
                tenantId, FraudBlacklist.BlacklistType.EMAIL, "risk@blocked.com"))
                .thenReturn(true);
        when(blacklistRepository.existsByTenantIdAndTypeAndValueAndActiveTrue(
                tenantId, FraudBlacklist.BlacklistType.IP_ADDRESS, "10.0.0.1"))
                .thenReturn(false);
        when(blacklistRepository.existsByTenantIdAndTypeAndValueAndActiveTrue(
                tenantId, FraudBlacklist.BlacklistType.DEVICE_FINGERPRINT, "device-x"))
                .thenReturn(false);

        when(analysisRepository.save(any(FraudAnalysis.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FraudAnalysis analysis = fraudService.analyzeOrder(
                orderId,
                buyerId,
                new BigDecimal("1500.00"),
                "risk@blocked.com",
                "10.0.0.1",
                "device-x"
        );

        assertThat(analysis.getDecision()).isEqualTo(FraudAnalysis.Decision.REJECTED);
        assertThat(analysis.getRiskScore()).isGreaterThanOrEqualTo(new BigDecimal("80"));
        assertThat(analysis.getReasons()).contains("Email in blacklist");
    }
}
