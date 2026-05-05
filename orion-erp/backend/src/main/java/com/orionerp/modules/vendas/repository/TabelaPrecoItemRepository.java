package com.orionerp.modules.vendas.repository;

import com.orionerp.modules.vendas.domain.TabelaPrecoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TabelaPrecoItemRepository extends JpaRepository<TabelaPrecoItem, Long> {
    Optional<TabelaPrecoItem> findByIdAndDeletedFalse(Long id);
    List<TabelaPrecoItem> findByTabelaPrecoIdAndDeletedFalse(Long tabelaPrecoId);
    boolean existsByTabelaPrecoIdAndProdutoIdAndDeletedFalse(Long tabelaPrecoId, Long produtoId);
}
