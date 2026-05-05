package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    Optional<Produto> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalse(Long empresaId, String codigo);

    boolean existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalseAndIdNot(Long empresaId, String codigo, Long id);
}
