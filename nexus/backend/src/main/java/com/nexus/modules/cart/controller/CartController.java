package com.nexus.modules.cart.controller;

import com.nexus.modules.cart.dto.*;
import com.nexus.modules.cart.service.CartService;
import com.nexus.shared.security.NexusPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "Shopping cart management")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get active cart")
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        UUID userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(cartService.getOrCreateCart(userId, sessionId));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody AddToCartRequest req,
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        UUID userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(cartService.addItem(userId, sessionId, req));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest req,
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        UUID userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(cartService.updateItem(userId, sessionId, itemId, req.getQuantity()));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeItem(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        UUID userId = principal != null ? principal.getUserId() : null;
        cartService.removeItem(userId, sessionId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/coupon")
    @Operation(summary = "Apply coupon to cart")
    public ResponseEntity<CartResponse> applyCoupon(
            @Valid @RequestBody ApplyCouponRequest req,
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        UUID userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(cartService.applyCoupon(userId, sessionId, req.getCode()));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Remove coupon from cart")
    public ResponseEntity<CartResponse> removeCoupon(
            @AuthenticationPrincipal NexusPrincipal principal,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        UUID userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(cartService.removeCoupon(userId, sessionId));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest cart into user cart after login")
    public ResponseEntity<CartResponse> mergeCart(
            @RequestHeader("X-Session-ID") String sessionId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return ResponseEntity.ok(cartService.mergeGuestCart(principal.getUserId(), sessionId));
    }
}
