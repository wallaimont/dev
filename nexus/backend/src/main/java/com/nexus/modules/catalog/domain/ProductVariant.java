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
@Table(name = "product_variants",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "sku"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(length = 255)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "promotional_price", precision = 12, scale = 2)
    private BigDecimal promotionalPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> attributes;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 50)
    private String barcode;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(length = 20)
    private String status = "ACTIVE";

    // Lazy-loaded stock
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "variant_id", insertable = false, updatable = false)
    private StockItem stock;

    public boolean isInStock() {
        return stock != null && stock.getAvailableQuantity() > 0;
    }

    public int getAvailableQuantity() {
        return stock != null ? stock.getAvailableQuantity() : 0;
    }
}
