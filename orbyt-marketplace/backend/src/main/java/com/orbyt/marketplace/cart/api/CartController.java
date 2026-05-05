package com.orbyt.marketplace.cart.api;

import com.orbyt.marketplace.cart.api.dto.AddToCartRequest;
import com.orbyt.marketplace.cart.api.dto.CartResponse;
import com.orbyt.marketplace.cart.application.CartService;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return cartService.getCart(principal.getUserId());
    }

    @PostMapping("/items")
    public CartResponse addItem(@AuthenticationPrincipal AuthUserPrincipal principal,
                                @Valid @RequestBody AddToCartRequest request) {
        return cartService.addItem(principal.getUserId(), request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(@AuthenticationPrincipal AuthUserPrincipal principal,
                                   @PathVariable java.util.UUID itemId,
                                   @RequestParam int quantity) {
        return cartService.updateItemQuantity(principal.getUserId(), itemId, quantity);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@AuthenticationPrincipal AuthUserPrincipal principal,
                                   @PathVariable java.util.UUID itemId) {
        return cartService.removeItem(principal.getUserId(), itemId);
    }

    @DeleteMapping
    public void clearCart(@AuthenticationPrincipal AuthUserPrincipal principal) {
        cartService.clearCart(principal.getUserId());
    }
}
