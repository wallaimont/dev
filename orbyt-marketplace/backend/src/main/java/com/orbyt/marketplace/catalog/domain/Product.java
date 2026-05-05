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
@Table(name = "products")
public class Product extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private BigDecimal promotionalPrice;

    @Column(nullable = false)
    private String currencyCode = "BRL";

    @Column(nullable = false)
    private String approvalStatus = "PENDING";
}
