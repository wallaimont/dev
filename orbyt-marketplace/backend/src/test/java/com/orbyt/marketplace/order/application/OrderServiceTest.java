package com.orbyt.marketplace.order.application;

import com.orbyt.marketplace.order.domain.Order;
import com.orbyt.marketplace.order.domain.OrderStatusHistory;
import com.orbyt.marketplace.order.repository.OrderGroupRepository;
import com.orbyt.marketplace.order.repository.OrderRepository;
import com.orbyt.marketplace.order.repository.OrderStatusHistoryRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private OrderStatusHistoryRepository statusHistoryRepository;

    @InjectMocks
    private OrderService orderService;

    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.set(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldUpdateOrderStatusAndCreateHistory() {
        UUID orderId = UUID.randomUUID();
        UUID changedBy = UUID.randomUUID();
        Order order = new Order();
        order.setTenantId(tenantId);
        order.setStatus("AWAITING_PAYMENT");

        when(orderRepository.findByIdAndTenantId(orderId, tenantId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order updated = orderService.updateStatus(orderId, "PROCESSING", changedBy);

        ArgumentCaptor<OrderStatusHistory> historyCaptor = ArgumentCaptor.forClass(OrderStatusHistory.class);
        verify(statusHistoryRepository).save(historyCaptor.capture());

        assertThat(updated.getStatus()).isEqualTo("PROCESSING");
        assertThat(historyCaptor.getValue().getTenantId()).isEqualTo(tenantId);
        assertThat(historyCaptor.getValue().getOrderId()).isEqualTo(orderId);
        assertThat(historyCaptor.getValue().getFromStatus()).isEqualTo("AWAITING_PAYMENT");
        assertThat(historyCaptor.getValue().getToStatus()).isEqualTo("PROCESSING");
        assertThat(historyCaptor.getValue().getChangedBy()).isEqualTo(changedBy);
    }

    @Test
    void shouldRejectCancelForDeliveredOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setTenantId(tenantId);
        order.setStatus("DELIVERED");

        when(orderRepository.findByIdAndTenantId(orderId, tenantId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cannot cancel order in status");

        verify(statusHistoryRepository, never()).save(any(OrderStatusHistory.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}