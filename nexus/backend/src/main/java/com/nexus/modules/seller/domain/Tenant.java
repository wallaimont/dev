package com.nexus.modules.seller.domain;

import com.nexus.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenantPlan plan = TenantPlan.STARTER;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "BRL";

    @Column(name = "default_locale", nullable = false, length = 10)
    private String defaultLocale = "pt-BR";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public enum TenantStatus { ACTIVE, SUSPENDED, TRIAL, CANCELLED }
    public enum TenantPlan   { STARTER, GROWTH, ENTERPRISE, CUSTOM }
}
