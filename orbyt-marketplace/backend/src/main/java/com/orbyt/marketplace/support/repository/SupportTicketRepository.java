package com.orbyt.marketplace.support.repository;

import com.orbyt.marketplace.support.domain.SupportTicket;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
    Page<SupportTicket> findByTenantIdAndUserIdOrderByCreatedAtDesc(UUID tenantId, UUID userId, Pageable pageable);
    Page<SupportTicket> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);
    long countByTenantIdAndStatus(UUID tenantId, String status);
}
