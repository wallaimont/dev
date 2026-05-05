package com.orionerp.modules.relatorios.service;

import com.orionerp.modules.relatorios.dto.RelatorioComissaoVendedorResponse;
import com.orionerp.modules.relatorios.dto.RelatorioVendasPeriodoResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioVendasService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public RelatorioVendasPeriodoResponse vendasPorPeriodo(Long empresaId, LocalDate inicio, LocalDate fim) {

        Object[] r = em.createQuery(
                        "SELECT COUNT(p), " +
                                "COALESCE(SUM(p.valorProdutos), 0), " +
                                "COALESCE(SUM(p.valorDesconto), 0), " +
                                "COALESCE(SUM(p.valorTotal), 0) " +
                                "FROM PedidoVenda p " +
                                "WHERE p.empresaId = :empresaId AND p.deleted = false " +
                                "AND p.dataPedido BETWEEN :inicio AND :fim " +
                                "AND p.status <> 'CANCELADO'",
                        Object[].class)
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();

        String periodo = inicio + " a " + fim;
        return new RelatorioVendasPeriodoResponse(
                periodo,
                (Long) r[0],
                (BigDecimal) r[1],
                (BigDecimal) r[2],
                (BigDecimal) r[3]
        );
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RelatorioComissaoVendedorResponse> comissoesPorVendedor(Long empresaId, LocalDate inicio, LocalDate fim) {

        List<Object[]> rows = em.createQuery(
                        "SELECT c.vendedorId, " +
                                "(SELECT f.nome FROM Funcionario f WHERE f.id = c.vendedorId), " +
                                "COALESCE(SUM(c.valorBase), 0), " +
                                "COALESCE(SUM(CASE WHEN c.status = 'PENDENTE' THEN c.valorComissao ELSE 0 END), 0), " +
                                "COALESCE(SUM(CASE WHEN c.status = 'PAGO' THEN c.valorComissao ELSE 0 END), 0) " +
                                "FROM Comissao c " +
                                "WHERE c.empresaId = :empresaId " +
                                "AND c.dataPagamento BETWEEN :inicio AND :fim " +
                                "GROUP BY c.vendedorId")
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        return rows.stream().map(r -> new RelatorioComissaoVendedorResponse(
                (Long) r[0],
                r[1] != null ? (String) r[1] : "Vendedor " + r[0],
                (BigDecimal) r[2],
                (BigDecimal) r[3],
                (BigDecimal) r[4]
        )).toList();
    }
}
