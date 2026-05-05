package com.orionerp.modules.pcp.repository;

import com.orionerp.modules.pcp.domain.EstruturaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EstruturaProdutoRepository extends JpaRepository<EstruturaProduto, Long>, JpaSpecificationExecutor<EstruturaProduto> {
    Optional<EstruturaProduto> findByIdAndDeletedFalse(Long id);
    List<EstruturaProduto> findByProdutoPaiIdAndDeletedFalse(Long produtoPaiId);
}
