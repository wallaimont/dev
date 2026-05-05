package com.orionerp.modules.pcp.repository;

import com.orionerp.modules.pcp.domain.OrdemProducaoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrdemProducaoItemRepository extends JpaRepository<OrdemProducaoItem, Long>, JpaSpecificationExecutor<OrdemProducaoItem> {
    Optional<OrdemProducaoItem> findByIdAndDeletedFalse(Long id);
    List<OrdemProducaoItem> findByOrdemProducaoIdAndDeletedFalse(Long ordemProducaoId);
}
