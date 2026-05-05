package com.orionerp.modules.contabilidade.repository;

import com.orionerp.modules.contabilidade.domain.PlanoContas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PlanoContasRepository extends JpaRepository<PlanoContas, Long>, JpaSpecificationExecutor<PlanoContas> {
    Optional<PlanoContas> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
