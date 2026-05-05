package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.DepartamentoRh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DepartamentoRhRepository extends JpaRepository<DepartamentoRh, Long>, JpaSpecificationExecutor<DepartamentoRh> {
    Optional<DepartamentoRh> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
