package com.nexus.modules.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder public class SellerDetailResponse {
    public UUID id;
    public UUID userId;
    public String document;
    public String companyName;
    public String tradeName;
    public String status;
    public String sellerType;
    public BigDecimal commissionRate;
    public String level;
    public boolean isPremium;
    public StoreResponse store;
    public Instant createdAt;
}
