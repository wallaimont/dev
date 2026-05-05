package com.orbyt.marketplace.engagement.repository;

import com.orbyt.marketplace.engagement.domain.Chat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findByTenantIdAndBuyerIdOrderByLastMessageAtDesc(UUID tenantId, UUID buyerId);
    List<Chat> findByTenantIdAndSellerIdOrderByLastMessageAtDesc(UUID tenantId, UUID sellerId);
    Optional<Chat> findByTenantIdAndBuyerIdAndSellerId(UUID tenantId, UUID buyerId, UUID sellerId);
}
