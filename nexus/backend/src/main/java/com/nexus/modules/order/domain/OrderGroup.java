package com.nexus.modules.order.domain;

import com.nexus.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "order_groups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderGroup extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderGroupStatus status = OrderGroupStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "shipping_cost", precision = 12, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal commissionRate;

    @Column(name = "commission_amt", nullable = false, precision = 12, scale = 2)
    private BigDecimal commissionAmt;

    @Column(name = "seller_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellerAmount;

    @Column(name = "payout_id")
    private UUID payoutId;

    @Column(name = "payout_at")
    private Instant payoutAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_group_id")
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}
