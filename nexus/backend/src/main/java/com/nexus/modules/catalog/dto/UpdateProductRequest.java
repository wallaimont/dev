package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class UpdateProductRequest {
    public String     name;
    public String     description;
    public UUID       categoryId;
    public BigDecimal basePrice;
    public BigDecimal promotionalPrice;
    public String     shortDescription;
}
