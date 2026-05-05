package com.nexus.modules.notification.repository;

import com.nexus.modules.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdAndTenantIdAndReadAtIsNull(UUID userId, UUID tenantId);

    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.readAt = :now WHERE n.userId = :userId AND n.tenantId = :tenantId AND n.readAt IS NULL")
    void markAllReadByUserAndTenant(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId, @Param("now") Instant now);
}
