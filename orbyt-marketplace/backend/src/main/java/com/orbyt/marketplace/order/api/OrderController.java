package com.orbyt.marketplace.order.api;

import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import com.orbyt.marketplace.order.application.OrderService;
import com.orbyt.marketplace.order.domain.Order;
import com.orbyt.marketplace.order.domain.OrderGroup;
import com.orbyt.marketplace.order.domain.OrderStatusHistory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    public List<OrderGroup> getMyOrders(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return orderService.getMyOrders(principal.getUserId());
    }

    @GetMapping("/seller")
    public List<Order> getSellerOrders(@RequestParam UUID sellerId) {
        return orderService.getSellerOrders(sellerId);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable UUID id) {
        return orderService.getOrder(id);
    }

    @GetMapping("/groups/{id}")
    public OrderGroup getOrderGroup(@PathVariable UUID id) {
        return orderService.getOrderGroup(id);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable UUID id, @RequestBody UpdateStatusRequest req) {
        return orderService.updateStatus(id, req.status(), req.changedBy());
    }

    @PostMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable UUID id) {
        return orderService.cancelOrder(id);
    }

    @GetMapping("/{id}/history")
    public List<OrderStatusHistory> getOrderHistory(@PathVariable UUID id) {
        return orderService.getOrderHistory(id);
    }

    public record UpdateStatusRequest(String status, UUID changedBy) {}
}
