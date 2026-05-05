package com.nexus.modules.order.dto;

import com.nexus.modules.order.domain.OrderGroupStatus;
import com.nexus.modules.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @Builder public class OrderDetailResponse {
    public UUID id;
    public String orderNumber;
    public OrderStatus status;
    public BigDecimal subtotal;
    public BigDecimal shippingTotal;
    public BigDecimal discountTotal;
    public BigDecimal total;
    public String currencyCode;
    public List<OrderGroupDto> groups;
    public AddressDto shippingAddress;
    public List<OrderStatusHistoryDto> history;
    public Instant createdAt;
}
