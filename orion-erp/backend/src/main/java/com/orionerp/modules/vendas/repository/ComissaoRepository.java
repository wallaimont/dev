package com.orionerp.modules.vendas.repository;

import com.orionerp.modules.vendas.domain.Comissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComissaoRepository extends JpaRepository<Comissao, Long> {

    List<Comissao> findByEmpresaIdAndVendedorId(Long empresaId, Long vendedorId);

    List<Comissao> findByPedidoVendaId(Long pedidoVendaId);

    List<Comissao> findByVendedorIdAndStatus(Long vendedorId, String status);
}
