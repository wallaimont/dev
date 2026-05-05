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

@Data public class AddressDto {
    public String recipientName;
    public String street;
    public String number;
    public String complement;
    public String neighborhood;
    public String city;
    public String state;
    public String zipCode;
}
