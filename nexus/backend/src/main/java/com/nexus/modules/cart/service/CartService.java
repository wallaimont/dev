package com.nexus.modules.cart.service;

import com.nexus.modules.cart.domain.Cart;
import com.nexus.modules.cart.domain.CartItem;
import com.nexus.modules.cart.dto.*;
import com.nexus.modules.cart.repository.CartRepository;
import com.nexus.modules.catalog.domain.ProductVariant;
import com.nexus.modules.catalog.repository.ProductVariantRepository;
import com.nexus.modules.coupon.service.CouponService;
import com.nexus.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final CouponService couponService;

    @Transactional(readOnly = true)
    public CartResponse getOrCreateCart(UUID userId, String sessionId) {
        Cart cart = findActiveCart(userId, sessionId)
            .orElseGet(() -> createNewCart(userId, sessionId));
        return toResponse(cart);
    }

    public CartResponse addItem(UUID userId, String sessionId, AddToCartRequest req) {
        Cart cart = findActiveCart(userId, sessionId)
            .orElseGet(() -> createNewCart(userId, sessionId));

        ProductVariant variant = variantRepository.findById(req.getVariantId())
            .orElseThrow(() -> new ItemNotFoundException("Variant not found: " + req.getVariantId()));

        if (!variant.isInStock() || variant.getAvailableQuantity() < req.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }

        Optional<CartItem> existing = cart.getItems().stream()
            .filter(i -> i.getVariantId().equals(req.getVariantId()))
            .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + req.getQuantity();
            if (newQty > variant.getAvailableQuantity()) {
                throw new InsufficientStockException("Cannot add more than available stock");
            }
            item.setQuantity(newQty);
        } else {
            CartItem item = CartItem.builder()
                .cartId(cart.getId())
                .variantId(variant.getId())
                .productId(variant.getProductId())
                .sellerId(req.getSellerId())
                .quantity(req.getQuantity())
                .unitPrice(variant.getPrice())
                .sku(variant.getSku())
                .build();
            cart.getItems().add(item);
        }

        cart.setUpdatedAt(Instant.now());
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse updateItem(UUID userId, String sessionId, UUID itemId, int quantity) {
        Cart cart = getOwnedCart(userId, sessionId);

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ItemNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            ProductVariant variant = variantRepository.findById(item.getVariantId())
                .orElseThrow(() -> new ItemNotFoundException("Variant not found"));
            if (variant.getAvailableQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock");
            }
            item.setQuantity(quantity);
        }

        cart.setUpdatedAt(Instant.now());
        return toResponse(cartRepository.save(cart));
    }

    public void removeItem(UUID userId, String sessionId, UUID itemId) {
        Cart cart = getOwnedCart(userId, sessionId);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cartRepository.save(cart);
    }

    public CartResponse applyCoupon(UUID userId, String sessionId, String code) {
        Cart cart = getOwnedCart(userId, sessionId);

        BigDecimal discount = couponService.calculateDiscount(
            code, cart.getSubtotal(), TenantContext.getTenantId(), userId);

        cart.setCouponCode(code);
        cart.setCouponDiscount(discount);
        cart.setUpdatedAt(Instant.now());
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse removeCoupon(UUID userId, String sessionId) {
        Cart cart = getOwnedCart(userId, sessionId);
        cart.setCouponCode(null);
        cart.setCouponDiscount(BigDecimal.ZERO);
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse mergeGuestCart(UUID userId, String sessionId) {
        Optional<Cart> guestCart = cartRepository
            .findBySessionIdAndStatusAndTenantId(sessionId, Cart.CartStatus.ACTIVE,
                                                  TenantContext.getTenantId());
        if (guestCart.isEmpty()) return getOrCreateCart(userId, null);

        Cart userCart = findActiveCart(userId, null)
            .orElseGet(() -> createNewCart(userId, null));

        for (CartItem guestItem : guestCart.get().getItems()) {
            boolean exists = userCart.getItems().stream()
                .anyMatch(i -> i.getVariantId().equals(guestItem.getVariantId()));
            if (!exists) {
                guestItem.setCartId(userCart.getId());
                userCart.getItems().add(guestItem);
            }
        }

        guestCart.get().setStatus(Cart.CartStatus.ABANDONED);
        cartRepository.save(guestCart.get());

        return toResponse(cartRepository.save(userCart));
    }

    public Cart getActiveCart(UUID userId) {
        return cartRepository.findByUserIdAndStatusAndTenantId(
            userId, Cart.CartStatus.ACTIVE, TenantContext.getTenantId())
            .orElseThrow(() -> new ItemNotFoundException("No active cart found"));
    }

    public void convertToOrder(UUID cartId) {
        cartRepository.findById(cartId).ifPresent(cart -> {
            cart.setStatus(Cart.CartStatus.CONVERTED);
            cartRepository.save(cart);
        });
    }

    private Optional<Cart> findActiveCart(UUID userId, String sessionId) {
        UUID tenantId = TenantContext.getTenantId();
        if (userId != null) {
            return cartRepository.findByUserIdAndStatusAndTenantId(
                userId, Cart.CartStatus.ACTIVE, tenantId);
        }
        if (sessionId != null) {
            return cartRepository.findBySessionIdAndStatusAndTenantId(
                sessionId, Cart.CartStatus.ACTIVE, tenantId);
        }
        return Optional.empty();
    }

    private Cart createNewCart(UUID userId, String sessionId) {
        Cart cart = Cart.builder()
            .tenantId(TenantContext.getTenantId())
            .userId(userId)
            .sessionId(sessionId)
            .status(Cart.CartStatus.ACTIVE)
            .build();
        return cartRepository.save(cart);
    }

    private Cart getOwnedCart(UUID userId, String sessionId) {
        return findActiveCart(userId, sessionId)
            .orElseThrow(() -> new ItemNotFoundException("Cart not found"));
    }

    private CartResponse toResponse(Cart cart) {
        return CartResponse.builder()
            .id(cart.getId())
            .itemCount(cart.getTotalItemCount())
            .subtotal(cart.getSubtotal())
            .couponCode(cart.getCouponCode())
            .couponDiscount(cart.getCouponDiscount())
            .items(cart.getItems().stream().map(this::toItemDto).toList())
            .build();
    }

    private CartItemDto toItemDto(CartItem item) {
        return CartItemDto.builder()
            .id(item.getId())
            .variantId(item.getVariantId())
            .productId(item.getProductId())
            .sku(item.getSku())
            .quantity(item.getQuantity())
            .unitPrice(item.getUnitPrice())
            .lineTotal(item.getLineTotal())
            .build();
    }

    public static class ItemNotFoundException extends RuntimeException {
        public ItemNotFoundException(String msg) { super(msg); }
    }
    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String msg) { super(msg); }
    }
}
