package com.orionerp.modules.contabilidade.repository;

import com.orionerp.modules.contabilidade.domain.LancamentoContabil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LancamentoContabilRepository extends JpaRepository<LancamentoContabil, Long>, JpaSpecificationExecutor<LancamentoContabil> {
    Optional<LancamentoContabil> findByIdAndDeletedFalse(Long id);
}
