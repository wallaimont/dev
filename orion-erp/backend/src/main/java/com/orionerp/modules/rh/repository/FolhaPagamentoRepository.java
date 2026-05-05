package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.FolhaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamento, Long>, JpaSpecificationExecutor<FolhaPagamento> {
    Optional<FolhaPagamento> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndAnoAndMesAndTipoAndDeletedFalse(Long empresaId, Integer ano, Integer mes, String tipo);
}
