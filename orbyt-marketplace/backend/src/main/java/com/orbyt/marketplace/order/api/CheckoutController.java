package com.orbyt.marketplace.order.api;

import com.orbyt.marketplace.order.api.dto.CheckoutRequest;
import com.orbyt.marketplace.order.api.dto.OrderGroupResponse;
import com.orbyt.marketplace.order.application.CheckoutService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/checkout/preview")
    public Map<String, Object> preview(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.preview(request);
    }

    @PostMapping("/checkout")
    public OrderGroupResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }
}
