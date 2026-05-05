package com.orbyt.marketplace.cart.repository;

import com.orbyt.marketplace.cart.domain.Cart;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByTenantIdAndUserId(UUID tenantId, UUID userId);
    Optional<Cart> findByTenantIdAndSessionId(UUID tenantId, String sessionId);
}
