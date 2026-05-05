package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @Builder
public class ProductDetailResponse {
    public UUID       id;
    public String     name;
    public String     slug;
    public String     description;
    public String     shortDescription;
    public BigDecimal basePrice;
    public BigDecimal promotionalPrice;
    public BigDecimal effectivePrice;
    public BigDecimal avgRating;
    public Integer    totalReviews;
    public Integer    totalSold;
    public Product.ProductStatus status;
    public String     currencyCode;
    public UUID       categoryId;
    public UUID       sellerId;
    public UUID       storeId;
    public List<ProductVariantDto> variants;
    public List<ProductImageDto>   images;
}
