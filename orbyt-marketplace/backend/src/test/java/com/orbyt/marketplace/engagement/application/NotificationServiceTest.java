package com.orbyt.marketplace.engagement.application;

import com.orbyt.marketplace.engagement.domain.Notification;
import com.orbyt.marketplace.engagement.realtime.RealtimeMessagingService;
import com.orbyt.marketplace.engagement.repository.NotificationRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private RealtimeMessagingService realtimeMessagingService;

    @InjectMocks
    private NotificationService notificationService;

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
    void shouldCreateNotificationAndPushRealtimeUpdate() {
        UUID userId = UUID.randomUUID();

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepository.countByTenantIdAndUserIdAndIsReadFalse(tenantId, userId)).thenReturn(3L);

        Notification notification = notificationService.createNotification(
                userId,
                "ORDER_SHIPPED",
                "Pedido enviado",
                "Seu pedido saiu para entrega.",
                null
        );

        assertThat(notification.getTenantId()).isEqualTo(tenantId);
        assertThat(notification.getChannel()).isEqualTo("INTERNAL");
        assertThat(notification.getDeliveryStatus()).isEqualTo("SENT");
        assertThat(notification.getStatus()).isEqualTo("ACTIVE");
        assertThat(notification.getSentAt()).isNotNull();
        verify(realtimeMessagingService).pushNotification(eq(userId), any());
    }
}