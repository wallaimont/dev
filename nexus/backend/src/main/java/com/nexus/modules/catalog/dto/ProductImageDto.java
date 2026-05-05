package com.nexus.modules.catalog.dto;

import com.nexus.modules.catalog.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @AllArgsConstructor
public class ProductImageDto {
    public UUID    id;
    public String  url;
    public boolean isMain;
    public Integer sortOrder;
}
