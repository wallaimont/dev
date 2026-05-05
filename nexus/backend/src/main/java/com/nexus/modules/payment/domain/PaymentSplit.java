package com.nexus.modules.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_splits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "order_group_id", nullable = false)
    private UUID orderGroupId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "recipient_key", length = 255)
    private String recipientKey;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "seller_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellerAmount;

    @Column(name = "platform_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal platformFee;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SplitStatus status = SplitStatus.PENDING;

    @Column(name = "gateway_split_id", length = 255)
    private String gatewaySplitId;

    @Column(name = "transferred_at")
    private Instant transferredAt;
}
