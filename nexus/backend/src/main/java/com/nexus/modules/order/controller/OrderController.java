package com.nexus.modules.order.controller;

import com.nexus.modules.order.dto.*;
import com.nexus.modules.order.service.OrderService;
import com.nexus.shared.security.NexusPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "Create order from cart (checkout)")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> checkout(
            @Valid @RequestBody CheckoutRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(req, principal.getUserId()));
    }

    @GetMapping
    @Operation(summary = "List my orders (buyer)")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Page<OrderSummary>> listMyOrders(
            @AuthenticationPrincipal NexusPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                orderService.listBuyerOrders(principal.getUserId(), pageable));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order detail")
    @PreAuthorize("hasAnyRole('BUYER','SELLER','TENANT_ADMIN')")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.ok(
                orderService.getOrderDetail(orderId, principal.getUserId()));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order")
    @PreAuthorize("hasAnyRole('BUYER','TENANT_ADMIN')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody CancelOrderRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        orderService.cancelOrder(orderId, req.getReason(), principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    // Seller endpoints
    @GetMapping("/seller")
    @Operation(summary = "List seller orders")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<OrderSummary>> listSellerOrders(
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                orderService.listSellerOrders(principal.getUserId(), status, pageable));
    }

    @PostMapping("/groups/{groupId}/ship")
    @Operation(summary = "Mark order group as shipped")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> markShipped(
            @PathVariable UUID groupId,
            @Valid @RequestBody ShipOrderRequest req,
            @AuthenticationPrincipal NexusPrincipal principal) {
        orderService.markShipped(groupId, req.getTrackingCode(), req.getCarrier(), principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    // Admin endpoints
    @GetMapping("/admin")
    @Operation(summary = "Admin - list all orders")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Page<OrderSummary>> adminListOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID sellerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                orderService.adminListOrders(status, sellerId, pageable));
    }
}
