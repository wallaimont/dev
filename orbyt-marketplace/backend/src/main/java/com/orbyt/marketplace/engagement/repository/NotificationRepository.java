package com.orbyt.marketplace.engagement.repository;

import com.orbyt.marketplace.engagement.domain.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByTenantIdAndUserIdOrderByCreatedAtDesc(UUID tenantId, UUID userId, Pageable pageable);
    long countByTenantIdAndUserIdAndIsReadFalse(UUID tenantId, UUID userId);
}
