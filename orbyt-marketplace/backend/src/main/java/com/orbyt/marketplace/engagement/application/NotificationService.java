package com.orbyt.marketplace.engagement.application;

import com.orbyt.marketplace.engagement.realtime.RealtimeMessagingService;
import com.orbyt.marketplace.engagement.realtime.dto.NotificationRealtimeMessage;
import com.orbyt.marketplace.engagement.domain.Notification;
import com.orbyt.marketplace.engagement.repository.NotificationRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RealtimeMessagingService realtimeMessagingService;

    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByTenantIdAndUserIdOrderByCreatedAtDesc(
                TenantContext.require(), userId, pageable);
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByTenantIdAndUserIdAndIsReadFalse(
                TenantContext.require(), userId);
    }

    @Transactional
    public Notification createNotification(UUID userId, String type, String title, String body, String channel) {
        Notification n = new Notification();
        n.setTenantId(TenantContext.require());
        n.setUserId(userId);
        n.setNotificationType(type);
        n.setTitle(title);
        n.setBody(body);
        n.setChannel(channel != null ? channel : "INTERNAL");
        n.setSentAt(OffsetDateTime.now());
        n.setDeliveryStatus("SENT");
        n.setStatus("ACTIVE");
        Notification saved = notificationRepository.save(n);
        long unreadCount = getUnreadCount(userId);
        realtimeMessagingService.pushNotification(userId, toRealtimeMessage(saved, unreadCount));
        return saved;
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        n.setRead(true);
        n.setReadAt(OffsetDateTime.now());
        Notification saved = notificationRepository.save(n);
        long unreadCount = getUnreadCount(saved.getUserId());
        realtimeMessagingService.pushNotification(saved.getUserId(), toRealtimeMessage(saved, unreadCount));
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        UUID tenantId = TenantContext.require();
        var page = notificationRepository.findByTenantIdAndUserIdOrderByCreatedAtDesc(
                tenantId, userId, Pageable.unpaged());
        var changed = page.getContent().stream()
                .filter(n -> !n.isRead())
                .peek(n -> {
                    n.setRead(true);
                    n.setReadAt(OffsetDateTime.now());
                })
                .toList();
        notificationRepository.saveAll(changed);
        long unreadCount = getUnreadCount(userId);
        changed.forEach(n -> realtimeMessagingService.pushNotification(userId, toRealtimeMessage(n, unreadCount)));
    }

    private NotificationRealtimeMessage toRealtimeMessage(Notification notification, long unreadCount) {
        return new NotificationRealtimeMessage(
                notification.getId(),
                notification.getNotificationType(),
                notification.getTitle(),
                notification.getBody(),
                notification.isRead(),
                notification.getCreatedAt(),
                unreadCount
        );
    }
}
