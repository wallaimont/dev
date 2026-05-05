package com.orionerp.modules.contabilidade.repository;

import com.orionerp.modules.contabilidade.domain.CentroResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CentroResultadoRepository extends JpaRepository<CentroResultado, Long>, JpaSpecificationExecutor<CentroResultado> {
    Optional<CentroResultado> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
