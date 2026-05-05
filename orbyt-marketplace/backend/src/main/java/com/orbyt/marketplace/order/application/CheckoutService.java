package com.orbyt.marketplace.order.application;

import com.orbyt.marketplace.cart.domain.Cart;
import com.orbyt.marketplace.cart.domain.CartItem;
import com.orbyt.marketplace.cart.repository.CartRepository;
import com.orbyt.marketplace.catalog.domain.Product;
import com.orbyt.marketplace.catalog.repository.ProductRepository;
import com.orbyt.marketplace.order.api.dto.CheckoutRequest;
import com.orbyt.marketplace.order.api.dto.OrderGroupResponse;
import com.orbyt.marketplace.order.domain.Order;
import com.orbyt.marketplace.order.domain.OrderGroup;
import com.orbyt.marketplace.order.domain.OrderItem;
import com.orbyt.marketplace.order.repository.OrderGroupRepository;
import com.orbyt.marketplace.observability.MarketplaceMetrics;
import com.orbyt.marketplace.shared.event.DomainEventPublisher;
import com.orbyt.marketplace.shared.event.DomainEvent;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final DomainEventPublisher eventPublisher;
        private final MarketplaceMetrics marketplaceMetrics;

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.10");

    @Transactional(readOnly = true)
    public Map<String, Object> preview(CheckoutRequest request) {
        UUID tenantId = TenantContext.require();
        Cart cart = cartRepository.findById(request.cartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal shipping = cart.getShippingTotal();
        BigDecimal commission = subtotal.multiply(DEFAULT_COMMISSION_RATE);

        long sellerCount = cart.getItems().stream()
                .map(item -> productRepository.findById(item.getProductId()).map(Product::getStoreId).orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return Map.of(
                "cartId", cart.getId(),
                "currencyCode", cart.getCurrencyCode(),
                "subtotal", subtotal,
                "shipping", shipping,
                "discount", cart.getDiscount(),
                "total", cart.getTotal(),
                "platformCommission", commission,
                "sellers", sellerCount,
                "paymentMethods", List.of("PIX", "CREDIT_CARD", "BOLETO")
        );
    }

    @Transactional
        public OrderGroupResponse checkout(CheckoutRequest request) {
                Instant startedAt = Instant.now();
                String outcome = "SUCCESS";
        UUID tenantId = TenantContext.require();
                try {
                        Cart cart = cartRepository.findById(request.cartId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
                        UUID buyerId = cart.getUserId();

                        if (cart.getItems().isEmpty()) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
                        }

                        // Group items by store (seller)
                        Map<UUID, List<CartItem>> itemsByStore = cart.getItems().stream()
                                        .collect(Collectors.groupingBy(item -> {
                                                Product p = productRepository.findById(item.getProductId()).orElseThrow();
                                                return p.getStoreId();
                                        }));

                        OrderGroup group = new OrderGroup();
                        group.setTenantId(tenantId);
                        group.setBuyerId(buyerId);
                        group.setCurrencyCode(cart.getCurrencyCode());
                        group.setPaymentMethod(request.paymentMethod());
                        group.setShippingAddressId(request.shippingAddressId());
                        group.setStatus("AWAITING_PAYMENT");

                        BigDecimal groupSubtotal = BigDecimal.ZERO;
                        BigDecimal groupShipping = BigDecimal.ZERO;

                        for (var entry : itemsByStore.entrySet()) {
                                UUID storeId = entry.getKey();
                                List<CartItem> storeItems = entry.getValue();

                                Order order = new Order();
                                order.setOrderGroup(group);
                                order.setTenantId(tenantId);
                                order.setBuyerId(buyerId);
                                order.setStoreId(storeId);
                                order.setCurrencyCode(cart.getCurrencyCode());
                                order.setStatus("AWAITING_PAYMENT");

                                // Find seller for this store
                                Product firstProduct = productRepository.findById(storeItems.get(0).getProductId()).orElseThrow();
                                order.setSellerId(firstProduct.getStoreId()); // simplified

                                BigDecimal orderSubtotal = BigDecimal.ZERO;
                                for (CartItem cartItem : storeItems) {
                                        Product product = productRepository.findById(cartItem.getProductId()).orElseThrow();
                                        OrderItem orderItem = new OrderItem();
                                        orderItem.setTenantId(tenantId);
                                        orderItem.setProductId(cartItem.getProductId());
                                        orderItem.setVariantId(cartItem.getVariantId());
                                        orderItem.setSku(product.getSku());
                                        orderItem.setProductName(product.getName());
                                        orderItem.setQuantity(cartItem.getQuantity());
                                        orderItem.setUnitPrice(cartItem.getUnitPrice());
                                        orderItem.setSubtotal(cartItem.getSubtotal());
                                        orderItem.setStatus("ACTIVE");
                                        order.addItem(orderItem);
                                        orderSubtotal = orderSubtotal.add(cartItem.getSubtotal());
                                }

                                order.setSubtotal(orderSubtotal);
                                order.setTotal(orderSubtotal.add(order.getShippingCost()));
                                order.calculateCommission(DEFAULT_COMMISSION_RATE);
                                group.getOrders().add(order);
                                groupSubtotal = groupSubtotal.add(orderSubtotal);
                        }

                        group.setSubtotal(groupSubtotal);
                        group.setShippingTotal(groupShipping);
                        group.setDiscountTotal(cart.getDiscount());
                        group.setGrandTotal(groupSubtotal.add(groupShipping).subtract(cart.getDiscount()));

                        OrderGroup saved = orderGroupRepository.save(group);

                        // Clear the cart
                        cart.getItems().clear();
                        cart.recalculate();
                        cartRepository.save(cart);

                        // Publish domain event
                        eventPublisher.publish(new DomainEvent(
                                        "order.created", "OrderGroup", saved.getId(),
                                        Map.of("buyerId", buyerId, "grandTotal", saved.getGrandTotal(), "paymentMethod", request.paymentMethod())
                        ));

                        marketplaceMetrics.recordCheckoutOutcome(
                                        "SUCCESS",
                                        request.paymentMethod(),
                                        saved.getGrandTotal()
                        );
                        return toResponse(saved);
                } catch (ResponseStatusException ex) {
                        outcome = "HTTP_" + ex.getStatusCode().value();
                        marketplaceMetrics.recordCheckoutOutcome(outcome, request.paymentMethod(), BigDecimal.ZERO);
                        throw ex;
                } catch (RuntimeException ex) {
                        outcome = "ERROR";
                        marketplaceMetrics.recordCheckoutOutcome(outcome, request.paymentMethod(), BigDecimal.ZERO);
                        throw ex;
                } finally {
                        marketplaceMetrics.recordCheckoutDuration(outcome, Duration.between(startedAt, Instant.now()));
                }
    }

    public OrderGroupResponse toResponse(OrderGroup group) {
        var orders = group.getOrders().stream().map(o -> {
            var items = o.getItems().stream().map(i ->
                    new OrderGroupResponse.OrderItemResponse(
                            i.getId(), i.getProductId(), i.getSku(), i.getProductName(),
                            i.getQuantity(), i.getUnitPrice(), i.getSubtotal()
                    )).toList();
            return new OrderGroupResponse.OrderResponse(
                    o.getId(), o.getSellerId(), o.getStoreId(), o.getSubtotal(),
                    o.getShippingCost(), o.getTotal(), o.getCommissionAmount(),
                    o.getSellerNet(), o.getStatus(), o.getTrackingCode(),
                    o.getShippingMethod(), items);
        }).toList();

        return new OrderGroupResponse(
                group.getId(), group.getBuyerId(), group.getCurrencyCode(),
                group.getSubtotal(), group.getShippingTotal(), group.getDiscountTotal(),
                group.getGrandTotal(), group.getPaymentMethod(), group.getStatus(),
                orders, group.getCreatedAt().toString());
    }
}
