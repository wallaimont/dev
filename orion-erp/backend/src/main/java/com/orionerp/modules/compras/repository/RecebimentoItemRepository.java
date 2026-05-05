package com.orionerp.modules.compras.repository;

import com.orionerp.modules.compras.domain.RecebimentoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecebimentoItemRepository extends JpaRepository<RecebimentoItem, Long> {

    List<RecebimentoItem> findByRecebimentoId(Long recebimentoId);
}
