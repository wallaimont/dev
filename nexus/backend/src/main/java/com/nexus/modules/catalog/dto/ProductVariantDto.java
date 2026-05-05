package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @Builder
public class ProductVariantDto {
    public UUID       id;
    public String     sku;
    public String     name;
    public BigDecimal price;
    public BigDecimal promotionalPrice;
    public Map<String, String> attributes;
    public String     imageUrl;
    public int        availableQuantity;
    public boolean    inStock;
}
