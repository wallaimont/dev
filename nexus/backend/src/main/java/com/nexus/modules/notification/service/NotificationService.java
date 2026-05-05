package com.nexus.modules.notification.service;

import com.nexus.modules.notification.domain.Notification;
import com.nexus.modules.notification.repository.NotificationRepository;
import com.nexus.modules.payment.events.PaymentApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailChannel emailChannel;
    private final PushChannel pushChannel;

    @KafkaListener(topics = "nexus.orders", groupId = "notification-service")
    @Transactional
    public void onOrderEvent(String eventJson) {
        log.debug("Order event received for notification: {}", eventJson);
    }

    @KafkaListener(topics = "nexus.payments", groupId = "notification-service")
    @Transactional
    public void onPaymentApproved(PaymentApprovedEvent event) {
        sendInApp(event.getTenantId(), findBuyerByOrder(event.getOrderId()),
            "payment.approved.title", "payment.approved.body", "ORDER_CONFIRMED");
        sendEmail(event.getTenantId(), findBuyerByOrder(event.getOrderId()),
            "order-confirmed", event);
    }

    public void sendInApp(UUID tenantId, UUID userId, String title, String body, String type) {
        Notification n = Notification.builder()
            .tenantId(tenantId)
            .userId(userId)
            .type(type)
            .title(title)
            .body(body)
            .channel(Notification.NotificationChannel.IN_APP)
            .build();
        repository.save(n);
    }

    public void sendEmail(UUID tenantId, UUID userId, String template, Object data) {
        emailChannel.send(tenantId, userId, template, data);
    }

    public List<Notification> getUnread(UUID userId, UUID tenantId) {
        return repository.findByUserIdAndTenantIdAndReadAtIsNull(userId, tenantId);
    }

    @Transactional
    public void markRead(UUID notificationId, UUID userId) {
        repository.findByIdAndUserId(notificationId, userId).ifPresent(n -> {
            n.setReadAt(Instant.now());
            repository.save(n);
        });
    }

    @Transactional
    public void markAllRead(UUID userId, UUID tenantId) {
        repository.markAllReadByUserAndTenant(userId, tenantId, Instant.now());
    }

    private UUID findBuyerByOrder(UUID orderId) {
        return UUID.randomUUID();
    }
}
