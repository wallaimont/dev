package com.nexus.modules.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seller_payouts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SellerPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "period_start", nullable = false)
    private java.time.LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private java.time.LocalDate periodEnd;

    @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "fees_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal feesAmount;

    @Column(name = "net_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal netAmount;

    @Column(length = 20)
    private String status = "PENDING";

    @Column(name = "pix_key", length = 255)
    private String pixKey;

    @Column(name = "transfer_id", length = 255)
    private String transferId;

    @Column(name = "transferred_at")
    private Instant transferredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
