package com.nexus.modules.cart.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data public class AddToCartRequest {
    public UUID variantId;
    public UUID sellerId;
    public int quantity;
}
