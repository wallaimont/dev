package com.orionerp.modules.fiscal.repository;

import com.orionerp.modules.fiscal.domain.NaturezaOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface NaturezaOperacaoRepository extends JpaRepository<NaturezaOperacao, Long>,
        JpaSpecificationExecutor<NaturezaOperacao> {

    Optional<NaturezaOperacao> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
