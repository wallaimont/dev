package com.nexus.modules.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data public class UpdateSellerRequest {
    public String companyName;
    public String tradeName;
}
