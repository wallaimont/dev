package com.orbyt.marketplace.fraud.domain;

import com.orbyt.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "fraud_analysis")
@Getter @Setter @NoArgsConstructor
public class FraudAnalysis extends BaseEntity {

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID buyerId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Decision decision;

    @Column(length = 2000)
    private String reasons;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 500)
    private String deviceFingerprint;

    public enum Decision {
        APPROVED, REVIEW, REJECTED
    }

    public static FraudAnalysis create(UUID tenantId, UUID orderId, UUID buyerId,
                                        BigDecimal score, String reasons) {
        FraudAnalysis fa = new FraudAnalysis();
        fa.setTenantId(tenantId);
        fa.setOrderId(orderId);
        fa.setBuyerId(buyerId);
        fa.setRiskScore(score);
        fa.setReasons(reasons);
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            fa.setDecision(Decision.REJECTED);
        } else if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
            fa.setDecision(Decision.REVIEW);
        } else {
            fa.setDecision(Decision.APPROVED);
        }
        return fa;
    }
}
