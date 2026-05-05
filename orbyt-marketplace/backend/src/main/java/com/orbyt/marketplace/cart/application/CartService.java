package com.orbyt.marketplace.cart.application;

import com.orbyt.marketplace.cart.api.dto.AddToCartRequest;
import com.orbyt.marketplace.cart.api.dto.CartResponse;
import com.orbyt.marketplace.cart.domain.Cart;
import com.orbyt.marketplace.cart.domain.CartItem;
import com.orbyt.marketplace.cart.repository.CartRepository;
import com.orbyt.marketplace.catalog.domain.Product;
import com.orbyt.marketplace.catalog.domain.Store;
import com.orbyt.marketplace.catalog.repository.ProductRepository;
import com.orbyt.marketplace.catalog.repository.StoreRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(UUID userId) {
        UUID tenantId = TenantContext.require();
        Cart cart = cartRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setTenantId(tenantId);
                    c.setUserId(userId);
                    return c;
                });
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(UUID userId, AddToCartRequest request) {
        UUID tenantId = TenantContext.require();
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Store store = storeRepository.findById(product.getStoreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        Cart cart = cartRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setTenantId(tenantId);
                    c.setUserId(userId);
                    c.setCurrencyCode(product.getCurrencyCode());
                    return c;
                });

        var existing = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.quantity());
            existing.get().calculateSubtotal();
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setTenantId(tenantId);
            item.setProductId(request.productId());
            item.setSellerId(store.getSellerId());
            item.setVariantId(request.variantId());
            item.setQuantity(request.quantity());
            item.setUnitPrice(product.getPromotionalPrice() != null ? product.getPromotionalPrice() : product.getPrice());
            item.setSubtotal(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(request.quantity())));
            cart.getItems().add(item);
        }

        cart.recalculate();
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Transactional
    public CartResponse updateItemQuantity(UUID userId, UUID itemId, int quantity) {
        UUID tenantId = TenantContext.require();
        Cart cart = cartRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        cart.getItems().remove(item);
                    } else {
                        item.setQuantity(quantity);
                        item.calculateSubtotal();
                    }
                });

        cart.recalculate();
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(UUID userId, UUID itemId) {
        return updateItemQuantity(userId, itemId, 0);
    }

    @Transactional
    public void clearCart(UUID userId) {
        UUID tenantId = TenantContext.require();
        cartRepository.findByTenantIdAndUserId(tenantId, userId)
                .ifPresent(cart -> {
                    cart.getItems().clear();
                    cart.recalculate();
                    cartRepository.save(cart);
                });
    }

    private CartResponse toResponse(Cart cart) {
        var items = cart.getItems().stream()
                .map(item -> new CartResponse.CartItemResponse(
                        item.getId(), item.getProductId(), item.getVariantId(),
                        item.getQuantity(), item.getUnitPrice(), item.getSubtotal()
                ))
                .toList();
        return new CartResponse(cart.getId(), cart.getUserId(), cart.getCurrencyCode(),
                cart.getSubtotal(), cart.getDiscount(), cart.getShippingTotal(),
                cart.getTotal(), items);
    }
}
