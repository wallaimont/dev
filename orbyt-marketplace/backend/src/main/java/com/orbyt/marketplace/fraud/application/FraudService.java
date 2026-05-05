package com.orbyt.marketplace.fraud.application;

import com.orbyt.marketplace.fraud.domain.FraudAnalysis;
import com.orbyt.marketplace.fraud.domain.FraudBlacklist;
import com.orbyt.marketplace.fraud.repository.FraudAnalysisRepository;
import com.orbyt.marketplace.fraud.repository.FraudBlacklistRepository;
import com.orbyt.marketplace.observability.MarketplaceMetrics;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudService {

    private final FraudAnalysisRepository analysisRepository;
    private final FraudBlacklistRepository blacklistRepository;
    private final MarketplaceMetrics marketplaceMetrics;

    @Transactional
    public FraudAnalysis analyzeOrder(UUID orderId, UUID buyerId, BigDecimal orderTotal,
                                      String email, String ipAddress, String deviceFingerprint) {
        UUID tenantId = TenantContext.require();
        BigDecimal score = BigDecimal.ZERO;
        List<String> reasons = new ArrayList<>();

        // Check blacklists
        if (email != null && blacklistRepository.existsByTenantIdAndTypeAndValueAndActiveTrue(
                tenantId, FraudBlacklist.BlacklistType.EMAIL, email)) {
            score = score.add(BigDecimal.valueOf(90));
            reasons.add("Email in blacklist");
            marketplaceMetrics.recordFraudBlacklistHit(FraudBlacklist.BlacklistType.EMAIL.name());
        }
        if (ipAddress != null && blacklistRepository.existsByTenantIdAndTypeAndValueAndActiveTrue(
                tenantId, FraudBlacklist.BlacklistType.IP_ADDRESS, ipAddress)) {
            score = score.add(BigDecimal.valueOf(40));
            reasons.add("IP address in blacklist");
            marketplaceMetrics.recordFraudBlacklistHit(FraudBlacklist.BlacklistType.IP_ADDRESS.name());
        }
        if (deviceFingerprint != null && blacklistRepository.existsByTenantIdAndTypeAndValueAndActiveTrue(
                tenantId, FraudBlacklist.BlacklistType.DEVICE_FINGERPRINT, deviceFingerprint)) {
            score = score.add(BigDecimal.valueOf(60));
            reasons.add("Device fingerprint in blacklist");
            marketplaceMetrics.recordFraudBlacklistHit(FraudBlacklist.BlacklistType.DEVICE_FINGERPRINT.name());
        }

        // High value order check
        if (orderTotal.compareTo(BigDecimal.valueOf(10000)) > 0) {
            score = score.add(BigDecimal.valueOf(20));
            reasons.add("High value order: " + orderTotal);
        }

        // Cap score at 100
        if (score.compareTo(BigDecimal.valueOf(100)) > 0) {
            score = BigDecimal.valueOf(100);
        }

        FraudAnalysis analysis = FraudAnalysis.create(tenantId, orderId, buyerId,
                score, String.join("; ", reasons));
        analysis.setIpAddress(ipAddress);
        analysis.setDeviceFingerprint(deviceFingerprint);

        log.info("Fraud analysis for order {}: score={}, decision={}", orderId, score, analysis.getDecision());
        FraudAnalysis saved = analysisRepository.save(analysis);
        marketplaceMetrics.recordFraudAnalysis(saved.getDecision().name(), saved.getRiskScore());
        return saved;
    }

    @Transactional
    public FraudBlacklist addToBlacklist(FraudBlacklist.BlacklistType type, String value, String reason) {
        FraudBlacklist entry = new FraudBlacklist();
        entry.setTenantId(TenantContext.require());
        entry.setType(type);
        entry.setValue(value);
        entry.setReason(reason);
        return blacklistRepository.save(entry);
    }
}
