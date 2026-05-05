package com.nexus.modules.cart.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data @Builder public class CartResponse {
    public UUID id;
    public int itemCount;
    public BigDecimal subtotal;
    public String couponCode;
    public BigDecimal couponDiscount;
    public List<CartItemDto> items;
}
