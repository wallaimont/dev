package com.nexus.modules.cart.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data @Builder public class CartItemDto {
    public UUID id;
    public UUID variantId;
    public UUID productId;
    public String sku;
    public int quantity;
    public BigDecimal unitPrice;
    public BigDecimal lineTotal;
}
