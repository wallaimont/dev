package com.orbyt.marketplace.cart.repository;

import com.orbyt.marketplace.cart.domain.CartItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}
