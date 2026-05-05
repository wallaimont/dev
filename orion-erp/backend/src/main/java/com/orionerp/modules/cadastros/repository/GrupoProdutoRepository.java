package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.GrupoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GrupoProdutoRepository extends JpaRepository<GrupoProduto, Long>, JpaSpecificationExecutor<GrupoProduto> {
    Optional<GrupoProduto> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
