package com.orbyt.marketplace.payment.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "commissions")
public class Commission extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID sellerId;

    @Column(nullable = false)
    private BigDecimal orderAmount;

    @Column(nullable = false)
    private BigDecimal commissionRate;

    @Column(nullable = false)
    private BigDecimal commissionAmount;

    @Column(nullable = false)
    private String currencyCode = "BRL";
}
