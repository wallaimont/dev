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
@Table(name = "stores")
public class Store extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID sellerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal reputationScore = BigDecimal.valueOf(5);

    @Column(nullable = false)
    private boolean premiumBadge = false;
}
