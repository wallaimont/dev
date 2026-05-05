package com.orbyt.marketplace.order.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "order_groups")
public class OrderGroup extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID buyerId;

    @Column(nullable = false)
    private String currencyCode = "BRL";

    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal shippingTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Column
    private UUID couponId;

    @Column
    private UUID shippingAddressId;

    @Column
    private String paymentMethod;

    @Column(length = 2000)
    private String notes;

    @OneToMany(mappedBy = "orderGroup", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
