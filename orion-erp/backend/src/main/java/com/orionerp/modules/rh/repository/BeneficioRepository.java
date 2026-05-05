package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long>, JpaSpecificationExecutor<Beneficio> {
    Optional<Beneficio> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
