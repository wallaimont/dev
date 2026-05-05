package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.SubgrupoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SubgrupoProdutoRepository extends JpaRepository<SubgrupoProduto, Long>, JpaSpecificationExecutor<SubgrupoProduto> {
    Optional<SubgrupoProduto> findByIdAndDeletedFalse(Long id);
}
