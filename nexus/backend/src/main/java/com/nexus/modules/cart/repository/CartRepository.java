package com.nexus.modules.cart.repository;

import com.nexus.modules.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndStatusAndTenantId(UUID userId, Cart.CartStatus status, UUID tenantId);
    Optional<Cart> findBySessionIdAndStatusAndTenantId(String sessionId, Cart.CartStatus status, UUID tenantId);
}
