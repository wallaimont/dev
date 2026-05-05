package com.orionerp.modules.estoque.repository;

import com.orionerp.modules.estoque.domain.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioItemRepository extends JpaRepository<InventarioItem, Long> {

    List<InventarioItem> findByInventarioId(Long inventarioId);
}
