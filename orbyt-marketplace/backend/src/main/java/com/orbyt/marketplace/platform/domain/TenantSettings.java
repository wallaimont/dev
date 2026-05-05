package com.orbyt.marketplace.platform.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tenant_settings")
public class TenantSettings extends TenantScopedEntity {

    @Column(nullable = false)
    private String brandName;

    @Column(nullable = false)
    private String primaryColor = "#F59E0B";

    @Column(nullable = false)
    private String secondaryColor = "#0F172A";

    @Column(nullable = false)
    private BigDecimal defaultCommissionRate = BigDecimal.valueOf(12.5);
}
