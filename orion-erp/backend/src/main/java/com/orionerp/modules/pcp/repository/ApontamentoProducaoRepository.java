package com.orionerp.modules.pcp.repository;

import com.orionerp.modules.pcp.domain.ApontamentoProducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ApontamentoProducaoRepository extends JpaRepository<ApontamentoProducao, Long>, JpaSpecificationExecutor<ApontamentoProducao> {
    Optional<ApontamentoProducao> findByIdAndDeletedFalse(Long id);
}
