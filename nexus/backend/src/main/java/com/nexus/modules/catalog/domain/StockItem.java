package com.nexus.modules.catalog.domain;

import com.nexus.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "stock_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "variant_id", nullable = false, unique = true)
    private UUID variantId;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Integer reserved = 0;

    @Column(name = "min_quantity")
    private Integer minQuantity = 0;

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Version
    private Long version; // Optimistic locking

    public int getAvailableQuantity() {
        return Math.max(0, quantity - reserved);
    }

    public boolean canReserve(int qty) {
        return getAvailableQuantity() >= qty;
    }

    public void reserve(int qty) {
        if (!canReserve(qty)) throw new InsufficientStockException(variantId, qty, getAvailableQuantity());
        this.reserved += qty;
        this.updatedAt = Instant.now();
    }

    public void release(int qty) {
        this.reserved = Math.max(0, this.reserved - qty);
        this.updatedAt = Instant.now();
    }

    public void deduct(int qty) {
        this.quantity = Math.max(0, this.quantity - qty);
        this.reserved = Math.max(0, this.reserved - qty);
        this.updatedAt = Instant.now();
    }

    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(UUID variantId, int requested, int available) {
            super("Insufficient stock for variant " + variantId +
                  ": requested=" + requested + ", available=" + available);
        }
    }
}
