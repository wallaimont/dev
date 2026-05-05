package com.orbyt.marketplace.platform.domain;

import com.orbyt.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private String defaultLocale = "pt-BR";

    @Column(nullable = false)
    private String currencyCode = "BRL";

    @Column
    private String primaryDomain;
}
