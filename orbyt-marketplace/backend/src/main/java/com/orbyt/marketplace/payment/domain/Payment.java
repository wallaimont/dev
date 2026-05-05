package com.orbyt.marketplace.payment.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID orderGroupId;

    @Column
    private UUID buyerId;

    @Column(nullable = false)
    private String paymentMethod = "PIX";

    @Column(nullable = false)
    private String currencyCode = "BRL";

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column
    private String gatewayProvider;

    @Column
    private String gatewayTransactionId;

    @Column
    private OffsetDateTime paidAt;

    @Column
    private String failureReason;
}
