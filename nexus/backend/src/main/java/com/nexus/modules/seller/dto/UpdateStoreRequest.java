package com.nexus.modules.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data public class UpdateStoreRequest {
    public String name;
    public String description;
    public String logoUrl;
    public String bannerUrl;
    public String policies;
    public String returnPolicy;
}
