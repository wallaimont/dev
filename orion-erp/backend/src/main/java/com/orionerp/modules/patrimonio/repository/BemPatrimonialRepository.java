package com.orionerp.modules.patrimonio.repository;

import com.orionerp.modules.patrimonio.domain.BemPatrimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BemPatrimonialRepository extends JpaRepository<BemPatrimonial, Long>, JpaSpecificationExecutor<BemPatrimonial> {
    Optional<BemPatrimonial> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
