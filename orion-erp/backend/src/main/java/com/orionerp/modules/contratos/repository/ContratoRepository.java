package com.orionerp.modules.contratos.repository;

import com.orionerp.modules.contratos.domain.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {
    Optional<Contrato> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndNumeroAndDeletedFalse(Long empresaId, String numero);
}
