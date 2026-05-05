package com.orionerp.modules.financeiro.repository;

import com.orionerp.modules.financeiro.domain.ConciliacaoBancariaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConciliacaoBancariaItemRepository extends JpaRepository<ConciliacaoBancariaItem, Long> {
    Optional<ConciliacaoBancariaItem> findByIdAndDeletedFalse(Long id);
    List<ConciliacaoBancariaItem> findByConciliacaoIdAndDeletedFalse(Long conciliacaoId);
}
