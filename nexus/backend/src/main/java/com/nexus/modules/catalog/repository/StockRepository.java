package com.nexus.modules.catalog.repository;

import com.nexus.modules.catalog.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<StockItem, UUID> {
    Optional<StockItem> findByVariantId(UUID variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockItem s WHERE s.variantId = :variantId")
    Optional<StockItem> findByVariantIdForUpdate(UUID variantId);

    @Query("SELECT s FROM StockItem s WHERE s.variantId IN :variantIds")
    List<StockItem> findAllByVariantIds(List<UUID> variantIds);
}
