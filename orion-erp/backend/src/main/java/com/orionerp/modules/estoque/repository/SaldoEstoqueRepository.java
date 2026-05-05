package com.orionerp.modules.estoque.repository;

import com.orionerp.modules.estoque.domain.SaldoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SaldoEstoqueRepository extends JpaRepository<SaldoEstoque, Long>, JpaSpecificationExecutor<SaldoEstoque> {

    Optional<SaldoEstoque> findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(
            Long empresaId, Long filialId, Long armazemId, Long produtoId, String lote);

    List<SaldoEstoque> findByProdutoId(Long produtoId);

    List<SaldoEstoque> findByEmpresaIdAndProdutoId(Long empresaId, Long produtoId);

    List<SaldoEstoque> findByEmpresaIdAndArmazemId(Long empresaId, Long armazemId);

    List<SaldoEstoque> findByEmpresaIdAndFilialIdAndArmazemId(Long empresaId, Long filialId, Long armazemId);
}
