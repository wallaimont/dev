package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @Builder
public class ProductSummaryResponse {
    public UUID       id;
    public String     name;
    public String     slug;
    public BigDecimal basePrice;
    public BigDecimal promotionalPrice;
    public BigDecimal effectivePrice;
    public boolean    isPromoActive;
    public BigDecimal avgRating;
    public Integer    totalReviews;
    public Integer    totalSold;
    public Product.ProductStatus status;
    public String     currencyCode;
    public boolean    hasVariants;
    public boolean    inStock;
    public String     mainImageUrl;
    public String     storeName;
    public UUID       storeId;
}
