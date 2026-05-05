package com.nexus.modules.order.repository;

import com.nexus.modules.order.domain.OrderGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderGroupRepository extends JpaRepository<OrderGroup, UUID> {
    List<OrderGroup> findByOrderId(UUID orderId);

    @Query("SELECT CASE WHEN COUNT(og) > 0 THEN true ELSE false END " +
           "FROM OrderGroup og JOIN User u ON og.sellerId = u.id " +
           "WHERE og.orderId = :orderId AND u.id = :userId")
    boolean existsByOrderIdAndSellerUserId(UUID orderId, UUID userId);
}
