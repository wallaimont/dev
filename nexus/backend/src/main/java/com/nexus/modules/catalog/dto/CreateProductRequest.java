package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateProductRequest {
    @NotBlank @Size(max = 500) public String name;
    @NotNull                   public UUID   categoryId;
    public UUID       brandId;
    @NotNull @DecimalMin("0.01") public BigDecimal basePrice;
    public BigDecimal promotionalPrice;
    public String     description;
    public String     shortDescription;
    public String     currencyCode;
    public String     condition;
    public Integer    weightGrams;
    public BigDecimal widthCm;
    public BigDecimal heightCm;
    public BigDecimal lengthCm;
    public String     metaTitle;
    public String     metaDescription;
    public List<VariantRequest> variants;
}
