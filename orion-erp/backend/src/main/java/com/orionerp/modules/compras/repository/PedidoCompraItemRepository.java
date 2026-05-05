package com.orionerp.modules.compras.repository;

import com.orionerp.modules.compras.domain.PedidoCompraItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoCompraItemRepository extends JpaRepository<PedidoCompraItem, Long> {

    List<PedidoCompraItem> findByPedidoCompraId(Long pedidoCompraId);
}
