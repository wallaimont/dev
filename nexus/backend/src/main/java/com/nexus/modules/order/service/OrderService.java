package com.nexus.modules.order.service;

import com.nexus.modules.cart.domain.Cart;
import com.nexus.modules.cart.service.CartService;
import com.nexus.modules.order.domain.*;
import com.nexus.modules.order.dto.*;
import com.nexus.modules.order.events.*;
import com.nexus.modules.order.repository.*;
import com.nexus.modules.payment.events.PaymentApprovedEvent;
import com.nexus.modules.catalog.service.StockService;
import com.nexus.shared.events.OutboxEventPublisher;
import com.nexus.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderGroupRepository groupRepository;
    private final OrderItemRepository itemRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final CartService cartService;
    private final StockService stockService;
    private final OutboxEventPublisher eventPublisher;
    private final CommissionCalculator commissionCalculator;

    // --------------------------------------------------------
    // CHECKOUT — Convert cart to order
    // --------------------------------------------------------

    /**
     * Main checkout flow:
     * 1. Validate stock for all items
     * 2. Reserve stock
     * 3. Create order with groups per seller
     * 4. Apply coupon
     * 5. Publish OrderCreatedEvent
     */
    public OrderResponse checkout(CheckoutRequest req, UUID buyerId) {
        UUID tenantId = TenantContext.getTenantId();

        Cart cart = cartService.getActiveCart(buyerId);
        validateCart(cart);

        // Reserve stock (throws if unavailable)
        stockService.reserveAll(cart);

        // Create main order
        Order order = new Order();
        order.setTenantId(tenantId);
        order.setBuyerId(buyerId);
        order.setOrderNumber(generateOrderNumber(tenantId));
        order.setStatus(OrderStatus.PENDING);
        order.setCurrencyCode(req.getCurrencyCode() != null ? req.getCurrencyCode() : "BRL");
        order.setShippingAddressId(req.getShippingAddressId());
        order.setNotes(req.getNotes());

        // Group items by seller
        Map<UUID, List<CartItemLine>> bySeller = groupBySeller(cart);
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal shippingTotal = BigDecimal.ZERO;

        List<OrderGroup> groups = new ArrayList<>();
        for (Map.Entry<UUID, List<CartItemLine>> entry : bySeller.entrySet()) {
            UUID sellerId = entry.getKey();
            List<CartItemLine> items = entry.getValue();

            BigDecimal groupSubtotal = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shippingCost = calculateShipping(sellerId, req.getShippingAddressId(), items);
            BigDecimal commRate = commissionCalculator.getRate(tenantId, sellerId);
            BigDecimal commAmt = groupSubtotal.multiply(commRate);
            BigDecimal sellerAmt = groupSubtotal.subtract(commAmt);

            OrderGroup group = new OrderGroup();
            group.setSellerId(sellerId);
            group.setStoreId(items.get(0).storeId());
            group.setStatus(OrderGroupStatus.PENDING);
            group.setSubtotal(groupSubtotal);
            group.setShippingCost(shippingCost);
            group.setCommissionRate(commRate);
            group.setCommissionAmt(commAmt);
            group.setSellerAmount(sellerAmt);

            groups.add(group);
            subtotal = subtotal.add(groupSubtotal);
            shippingTotal = shippingTotal.add(shippingCost);
        }

        BigDecimal discountTotal = applyDiscount(cart, req.getCouponCode());
        BigDecimal total = subtotal.add(shippingTotal).subtract(discountTotal);

        order.setSubtotal(subtotal);
        order.setShippingTotal(shippingTotal);
        order.setDiscountTotal(discountTotal);
        order.setTotal(total);
        order.setCouponCode(req.getCouponCode());
        order = orderRepository.save(order);

        // Save groups and items
        for (OrderGroup group : groups) {
            group.setOrderId(order.getId());
            group = groupRepository.save(group);
            saveOrderItems(group, bySeller.get(group.getSellerId()));
        }

        // Publish event
        final Order finalOrder = order;
        eventPublisher.publish(
            new OrderCreatedEvent(tenantId, order.getId(), buyerId, total),
            "order-created-" + order.getId()
        );

        // Abandon cart
        cartService.convertToOrder(cart.getId());

        log.info("Order created: {} total={}", order.getOrderNumber(), total);

        return buildOrderResponse(order, groups);
    }

    // --------------------------------------------------------
    // STATUS TRANSITIONS
    // --------------------------------------------------------

    public void confirmOrder(UUID orderId) {
        transitionOrder(orderId, OrderStatus.CONFIRMED, "Payment confirmed", null);
        publishOrderConfirmed(orderId);
    }

    public void markShipped(UUID orderGroupId, String trackingCode, String carrier, UUID operatorId) {
        OrderGroup group = groupRepository.findById(orderGroupId)
            .orElseThrow(() -> new OrderNotFoundException("Order group not found"));

        group.setStatus(OrderGroupStatus.SHIPPED);
        groupRepository.save(group);

        addGroupHistory(group, "SHIPPED", "Enviado: " + trackingCode, operatorId);

        // Check if all groups shipped → update main order
        updateMainOrderStatus(group.getOrderId());

        eventPublisher.publish(
            new OrderShippedEvent(TenantContext.getTenantId(), group.getOrderId(), orderGroupId, trackingCode),
            "order-shipped-" + orderGroupId
        );
    }

    public void cancelOrder(UUID orderId, String reason, UUID cancelledBy) {
        Order order = getOrder(orderId);

        if (!order.getStatus().isCancellable()) {
            throw new InvalidOrderTransitionException(
                "Cannot cancel order in status: " + order.getStatus());
        }

        // Release stock
        stockService.releaseReservations(orderId);

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());
        order.setCancelReason(reason);
        orderRepository.save(order);

        addOrderHistory(order, "CANCELLED", reason, cancelledBy);

        eventPublisher.publish(
            new OrderCancelledEvent(order.getTenantId(), orderId, reason),
            "order-cancelled-" + orderId
        );
    }

    // --------------------------------------------------------
    // EVENT LISTENERS — React to payment events
    // --------------------------------------------------------

    @EventListener
    public void onPaymentApproved(PaymentApprovedEvent event) {
        log.info("Payment approved for order: {}", event.getOrderId());
        confirmOrder(event.getOrderId());
    }

    // --------------------------------------------------------
    // QUERIES
    // --------------------------------------------------------

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(UUID orderId, UUID requesterId) {
        Order order = getOrder(orderId);

        // Authorization: buyer or seller in the order can view
        if (!order.getBuyerId().equals(requesterId) &&
            !isSellerInOrder(orderId, requesterId)) {
            throw new UnauthorizedException("Not authorized to view this order");
        }

        return buildDetailResponse(order);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderSummary> listBuyerOrders(
            UUID buyerId, org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findByBuyerIdAndTenantId(
            buyerId, TenantContext.getTenantId(), pageable)
            .map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderSummary> listSellerOrders(
            UUID sellerId, String status, org.springframework.data.domain.Pageable pageable) {
        return org.springframework.data.domain.Page.empty(pageable);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderSummary> adminListOrders(
            String status, UUID sellerId, org.springframework.data.domain.Pageable pageable) {
        return org.springframework.data.domain.Page.empty(pageable);
    }

    // --------------------------------------------------------
    // HELPERS
    // --------------------------------------------------------

    private String generateOrderNumber(UUID tenantId) {
        return "NX" + System.currentTimeMillis();
    }

    private void validateCart(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }
    }

    private Map<UUID, List<CartItemLine>> groupBySeller(Cart cart) {
        Map<UUID, List<CartItemLine>> map = new LinkedHashMap<>();
        cart.getItems().forEach(item -> {
            map.computeIfAbsent(item.getSellerId(), k -> new ArrayList<>())
               .add(new CartItemLine(item));
        });
        return map;
    }

    private BigDecimal calculateShipping(UUID sellerId, UUID addressId, List<CartItemLine> items) {
        // Delegate to ShippingService
        return BigDecimal.valueOf(15.90); // placeholder
    }

    private BigDecimal applyDiscount(Cart cart, String couponCode) {
        if (couponCode == null || couponCode.isBlank()) return BigDecimal.ZERO;
        // Delegate to CouponService
        return cart.getCouponDiscount() != null ? cart.getCouponDiscount() : BigDecimal.ZERO;
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findByIdAndTenantId(orderId, TenantContext.getTenantId())
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
    }

    private void transitionOrder(UUID orderId, OrderStatus newStatus, String comment, UUID changedBy) {
        Order order = getOrder(orderId);
        order.setStatus(newStatus);
        orderRepository.save(order);
        addOrderHistory(order, newStatus.name(), comment, changedBy);
    }

    private void addOrderHistory(Order order, String status, String comment, UUID changedBy) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.setOrderId(order.getId());
        h.setStatus(status);
        h.setComment(comment);
        h.setChangedBy(changedBy);
        historyRepository.save(h);
    }

    private void addGroupHistory(OrderGroup group, String status, String comment, UUID changedBy) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.setOrderId(group.getOrderId());
        h.setGroupId(group.getId());
        h.setStatus(status);
        h.setComment(comment);
        h.setChangedBy(changedBy);
        historyRepository.save(h);
    }

    private void updateMainOrderStatus(UUID orderId) {
        List<OrderGroup> allGroups = groupRepository.findByOrderId(orderId);
        boolean allShipped = allGroups.stream()
            .allMatch(g -> g.getStatus() == OrderGroupStatus.SHIPPED ||
                           g.getStatus() == OrderGroupStatus.DELIVERED);
        if (allShipped) {
            Order order = getOrder(orderId);
            order.setStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);
        }
    }

    private void saveOrderItems(OrderGroup group, List<CartItemLine> items) {
        for (CartItemLine line : items) {
            OrderItem item = new OrderItem();
            item.setOrderGroupId(group.getId());
            item.setOrderId(group.getOrderId());
            item.setVariantId(line.variantId());
            item.setProductId(line.productId());
            item.setSellerId(group.getSellerId());
            item.setProductName(line.productName());
            item.setSku(line.sku());
            item.setAttributes(line.attributes());
            item.setQuantity(line.quantity());
            item.setUnitPrice(line.unitPrice());
            item.setTotalPrice(line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())));
            itemRepository.save(item);
        }
    }

    private boolean isSellerInOrder(UUID orderId, UUID userId) {
        return groupRepository.existsByOrderIdAndSellerUserId(orderId, userId);
    }

    private void publishOrderConfirmed(UUID orderId) {
        Order order = getOrder(orderId);
        eventPublisher.publish(
            new OrderConfirmedEvent(order.getTenantId(), orderId, order.getBuyerId()),
            "order-confirmed-" + orderId
        );
    }

    // ---- Response builders -----------------------------------
    private OrderResponse buildOrderResponse(Order order, List<OrderGroup> groups) {
        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus())
            .total(order.getTotal())
            .currencyCode(order.getCurrencyCode())
            .createdAt(order.getCreatedAt())
            .build();
    }

    private OrderDetailResponse buildDetailResponse(Order order) {
        return OrderDetailResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus())
            .subtotal(order.getSubtotal())
            .shippingTotal(order.getShippingTotal())
            .discountTotal(order.getDiscountTotal())
            .total(order.getTotal())
            .currencyCode(order.getCurrencyCode())
            .createdAt(order.getCreatedAt())
            .build();
    }

    private OrderSummary toSummary(Order order) {
        return OrderSummary.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus())
            .total(order.getTotal())
            .itemCount(0)
            .createdAt(order.getCreatedAt())
            .build();
    }

    // Custom exceptions
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String msg) { super(msg); }
    }
    public static class EmptyCartException extends RuntimeException {
        public EmptyCartException(String msg) { super(msg); }
    }
    public static class InvalidOrderTransitionException extends RuntimeException {
        public InvalidOrderTransitionException(String msg) { super(msg); }
    }
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }
}
