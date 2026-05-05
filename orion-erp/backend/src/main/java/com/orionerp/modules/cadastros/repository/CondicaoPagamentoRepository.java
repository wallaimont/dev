package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.CondicaoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CondicaoPagamentoRepository extends JpaRepository<CondicaoPagamento, Long>, JpaSpecificationExecutor<CondicaoPagamento> {
    Optional<CondicaoPagamento> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
