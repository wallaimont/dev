package com.orionerp.modules.vendas.repository;

import com.orionerp.modules.vendas.domain.PedidoVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PedidoVendaRepository extends JpaRepository<PedidoVenda, Long>,
        JpaSpecificationExecutor<PedidoVenda> {

    Optional<PedidoVenda> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(Long empresaId, Long filialId, String numero);
}
