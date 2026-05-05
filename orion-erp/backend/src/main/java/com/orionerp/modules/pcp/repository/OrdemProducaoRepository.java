package com.orionerp.modules.pcp.repository;

import com.orionerp.modules.pcp.domain.OrdemProducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrdemProducaoRepository extends JpaRepository<OrdemProducao, Long>, JpaSpecificationExecutor<OrdemProducao> {
    Optional<OrdemProducao> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndNumeroAndDeletedFalse(Long empresaId, String numero);
}
