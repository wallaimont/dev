package com.orbyt.marketplace.order.application;

import com.orbyt.marketplace.order.domain.Order;
import com.orbyt.marketplace.order.domain.OrderGroup;
import com.orbyt.marketplace.order.domain.OrderStatusHistory;
import com.orbyt.marketplace.order.repository.OrderGroupRepository;
import com.orbyt.marketplace.order.repository.OrderRepository;
import com.orbyt.marketplace.order.repository.OrderStatusHistoryRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;

    @Transactional(readOnly = true)
    public List<OrderGroup> getMyOrders(UUID buyerId) {
        return orderGroupRepository.findByTenantIdAndBuyerIdOrderByCreatedAtDesc(TenantContext.require(), buyerId);
    }

    @Transactional(readOnly = true)
    public List<Order> getSellerOrders(UUID sellerId) {
        return orderRepository.findByTenantIdAndSellerIdOrderByCreatedAtDesc(TenantContext.require(), sellerId);
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID id) {
        return orderRepository.findByIdAndTenantId(id, TenantContext.require())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Transactional(readOnly = true)
    public OrderGroup getOrderGroup(UUID id) {
        return orderGroupRepository.findByIdAndTenantId(id, TenantContext.require())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order group not found"));
    }

    @Transactional
    public Order updateStatus(UUID id, String status, UUID changedBy) {
        Order order = getOrder(id);
        String oldStatus = order.getStatus();
        order.setStatus(status);
        statusHistoryRepository.save(buildHistory(id, oldStatus, status, changedBy));
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(UUID id) {
        Order order = getOrder(id);
        if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Cannot cancel order in status: " + order.getStatus());
        }
        String oldStatus = order.getStatus();
        order.setStatus("CANCELLED");
        statusHistoryRepository.save(buildHistory(id, oldStatus, "CANCELLED", null));
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getOrderHistory(UUID id) {
        return statusHistoryRepository.findByOrderIdAndTenantIdOrderByChangedAtDesc(id, TenantContext.require());
    }

    private OrderStatusHistory buildHistory(UUID orderId, String fromStatus, String toStatus, UUID changedBy) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setTenantId(TenantContext.require());
        history.setOrderId(orderId);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setChangedBy(changedBy);
        history.setChangedAt(OffsetDateTime.now());
        return history;
    }
}