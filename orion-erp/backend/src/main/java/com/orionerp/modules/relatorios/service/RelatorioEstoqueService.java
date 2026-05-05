package com.orionerp.modules.relatorios.service;

import com.orionerp.modules.relatorios.dto.RelatorioEstoquePosicaoResponse;
import com.orionerp.modules.relatorios.dto.RelatorioMovimentacaoEstoqueResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioEstoqueService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RelatorioEstoquePosicaoResponse> posicaoEstoque(Long empresaId) {

        List<Object[]> rows = em.createQuery(
                        "SELECT se.produto.id, se.produto.codigo, se.produto.nome, " +
                                "se.armazem.nome, se.quantidade, se.custoMedio, " +
                                "(se.quantidade * se.custoMedio), " +
                                "se.reservado, (se.quantidade - se.reservado) " +
                                "FROM SaldoEstoque se " +
                                "WHERE se.empresaId = :empresaId AND se.quantidade > 0 " +
                                "ORDER BY se.produto.nome")
                .setParameter("empresaId", empresaId)
                .getResultList();

        return rows.stream().map(r -> new RelatorioEstoquePosicaoResponse(
                (Long) r[0],
                (String) r[1],
                (String) r[2],
                (String) r[3],
                (BigDecimal) r[4],
                (BigDecimal) r[5],
                (BigDecimal) r[6],
                (BigDecimal) r[7],
                (BigDecimal) r[8]
        )).toList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RelatorioMovimentacaoEstoqueResponse> movimentacoes(Long empresaId, LocalDate inicio, LocalDate fim) {

        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = fim.plusDays(1).atStartOfDay();

        List<Object[]> rows = em.createQuery(
                        "SELECT CAST(me.createdAt AS LocalDate), me.produto.nome, me.tipo, " +
                                "me.quantidade, " +
                                "CONCAT(COALESCE(me.documentoTipo, ''), ' ', COALESCE(me.documentoNumero, '')), " +
                                "me.armazem.nome " +
                                "FROM MovimentacaoEstoque me " +
                                "WHERE me.empresaId = :empresaId " +
                                "AND me.createdAt >= :inicio AND me.createdAt < :fim " +
                                "ORDER BY me.createdAt DESC")
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicioDateTime)
                .setParameter("fim", fimDateTime)
                .getResultList();

        return rows.stream().map(r -> new RelatorioMovimentacaoEstoqueResponse(
                (LocalDate) r[0],
                (String) r[1],
                (String) r[2],
                (BigDecimal) r[3],
                (String) r[4],
                (String) r[5],
                null
        )).toList();
    }
}
