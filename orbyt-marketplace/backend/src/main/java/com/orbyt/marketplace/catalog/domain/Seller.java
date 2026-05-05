package com.orbyt.marketplace.catalog.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sellers")
public class Seller extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false)
    private String documentNumber;

    @Column(nullable = false)
    private String approvalStatus = "APPROVED";

    @Column(nullable = false)
    private boolean premiumBadge = false;

    @Column(nullable = false)
    private BigDecimal reputationScore = BigDecimal.valueOf(5);
}
