package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.TabelaPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TabelaPrecoRepository extends JpaRepository<TabelaPreco, Long>, JpaSpecificationExecutor<TabelaPreco> {
    Optional<TabelaPreco> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
