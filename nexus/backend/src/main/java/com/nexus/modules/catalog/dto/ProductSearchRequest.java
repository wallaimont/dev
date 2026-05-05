package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @Builder
public class ProductSearchRequest {
    private String     query;
    private UUID       categoryId;
    private UUID       brandId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double     minRating;
    private String     sortBy;
}
