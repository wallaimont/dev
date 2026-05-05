package com.orbyt.marketplace.engagement.api;

import com.orbyt.marketplace.engagement.application.NotificationService;
import com.orbyt.marketplace.engagement.domain.Notification;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Page<Notification> getMyNotifications(@AuthenticationPrincipal AuthUserPrincipal principal,
                                                  Pageable pageable) {
        return notificationService.getUserNotifications(principal.getUserId(), pageable);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return Map.of("count", notificationService.getUnreadCount(principal.getUserId()));
    }

    @PutMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @PutMapping("/read-all")
    public void markAllAsRead(@AuthenticationPrincipal AuthUserPrincipal principal) {
        notificationService.markAllAsRead(principal.getUserId());
    }
}
