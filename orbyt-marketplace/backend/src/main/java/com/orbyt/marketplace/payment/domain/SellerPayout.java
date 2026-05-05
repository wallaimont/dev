package com.orbyt.marketplace.payment.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "seller_payouts")
public class SellerPayout extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID sellerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currencyCode = "BRL";

    @Column
    private String payoutMethod;

    @Column
    private String pixKey;

    @Column
    private OffsetDateTime paidAt;

    @Column
    private LocalDate periodStart;

    @Column
    private LocalDate periodEnd;
}
