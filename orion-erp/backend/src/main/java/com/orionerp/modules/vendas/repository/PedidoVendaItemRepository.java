package com.orionerp.modules.vendas.repository;

import com.orionerp.modules.vendas.domain.PedidoVendaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoVendaItemRepository extends JpaRepository<PedidoVendaItem, Long> {

    List<PedidoVendaItem> findByPedidoVendaId(Long pedidoVendaId);
}
