package com.orbyt.marketplace.order.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends TenantScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_group_id", nullable = false)
    private OrderGroup orderGroup;

    @Column(nullable = false)
    private UUID sellerId;

    @Column
    private UUID storeId;

    @Column(nullable = false)
    private UUID buyerId;

    @Column(nullable = false)
    private String currencyCode = "BRL";

    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column
    private BigDecimal commissionRate = BigDecimal.ZERO;

    @Column
    private BigDecimal commissionAmount = BigDecimal.ZERO;

    @Column
    private BigDecimal sellerNet = BigDecimal.ZERO;

    @Column
    private String shippingMethod;

    @Column
    private String trackingCode;

    @Column
    private LocalDate estimatedDelivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public void calculateCommission(BigDecimal rate) {
        this.commissionRate = rate;
        this.commissionAmount = total.multiply(rate);
        this.sellerNet = total.subtract(commissionAmount);
    }
}
