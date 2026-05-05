package com.orionerp.modules.estoque.repository;

import com.orionerp.modules.estoque.domain.Armazem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ArmazemRepository extends JpaRepository<Armazem, Long>, JpaSpecificationExecutor<Armazem> {

    Optional<Armazem> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndFilialIdAndCodigoIgnoreCaseAndDeletedFalse(
            Long empresaId, Long filialId, String codigo);
}
