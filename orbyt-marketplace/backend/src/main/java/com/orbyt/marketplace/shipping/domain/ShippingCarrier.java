package com.orbyt.marketplace.shipping.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "shipping_carriers")
@Getter @Setter @NoArgsConstructor
public class ShippingCarrier extends TenantScopedEntity {

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String trackingUrlTemplate;

    @Column(length = 500)
    private String apiEndpoint;

    private boolean active = true;

    @Column(precision = 12, scale = 2)
    private BigDecimal flatRate;

    @Column(precision = 5, scale = 2)
    private BigDecimal freeShippingThreshold;
}
