package com.orbyt.marketplace.engagement.repository;

import com.orbyt.marketplace.engagement.domain.Wishlist;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByTenantIdAndUserId(UUID tenantId, UUID userId);
    Optional<Wishlist> findByTenantIdAndUserIdAndProductId(UUID tenantId, UUID userId, UUID productId);
    void deleteByTenantIdAndUserIdAndProductId(UUID tenantId, UUID userId, UUID productId);
}
