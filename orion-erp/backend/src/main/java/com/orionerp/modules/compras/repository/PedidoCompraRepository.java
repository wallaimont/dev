package com.orionerp.modules.compras.repository;

import com.orionerp.modules.compras.domain.PedidoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long>,
        JpaSpecificationExecutor<PedidoCompra> {

    Optional<PedidoCompra> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(Long empresaId, Long filialId, String numero);
}
