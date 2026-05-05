package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class VariantRequest {
    @NotBlank public String sku;
    public String     name;
    @NotNull @DecimalMin("0.01") public BigDecimal price;
    public Map<String, String> attributes;
    public String     barcode;
    public Integer    initialStock;
}
