package com.orionerp.modules.relatorios.service;

import com.orionerp.modules.relatorios.dto.RelatorioContasVencidasResponse;
import com.orionerp.modules.relatorios.dto.RelatorioFinanceiroResumo;
import com.orionerp.modules.relatorios.dto.RelatorioFluxoCaixaResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioFinanceiroService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public RelatorioFinanceiroResumo resumoFinanceiro(Long empresaId, LocalDate inicio, LocalDate fim) {

        BigDecimal totalReceber = em.createQuery(
                        "SELECT COALESCE(SUM(t.valorAberto), 0) FROM Titulo t " +
                                "WHERE t.empresaId = :empresaId AND t.tipo = 'RECEBER' " +
                                "AND t.dataEmissao BETWEEN :inicio AND :fim AND t.deleted = false",
                        BigDecimal.class)
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();

        BigDecimal totalPagar = em.createQuery(
                        "SELECT COALESCE(SUM(t.valorAberto), 0) FROM Titulo t " +
                                "WHERE t.empresaId = :empresaId AND t.tipo = 'PAGAR' " +
                                "AND t.dataEmissao BETWEEN :inicio AND :fim AND t.deleted = false",
                        BigDecimal.class)
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();

        Long vencidos = em.createQuery(
                        "SELECT COUNT(DISTINCT t.id) FROM Titulo t JOIN t.parcelas tp " +
                                "WHERE t.empresaId = :empresaId AND t.deleted = false " +
                                "AND tp.dataVencimento < CURRENT_DATE " +
                                "AND tp.status NOT IN ('QUITADO','CANCELADO')",
                        Long.class)
                .setParameter("empresaId", empresaId)
                .getSingleResult();

        String periodo = inicio + " a " + fim;
        BigDecimal saldo = totalReceber.subtract(totalPagar);

        return new RelatorioFinanceiroResumo(empresaId, periodo, totalReceber, totalPagar, saldo, vencidos);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RelatorioContasVencidasResponse> contasVencidas(Long empresaId) {

        List<Object[]> rows = em.createQuery(
                        "SELECT t.tipo, " +
                                "COALESCE(cli.nomeFantasia, cli.razaoSocial, forn.nomeFantasia, forn.razaoSocial, 'N/A'), " +
                                "t.numero, t.valorOriginal, (tp.valor - COALESCE(tp.valorPago, 0)), " +
                                "tp.dataVencimento " +
                                "FROM Titulo t JOIN t.parcelas tp " +
                                "LEFT JOIN t.cliente cli LEFT JOIN t.fornecedor forn " +
                                "WHERE t.empresaId = :empresaId AND t.deleted = false " +
                                "AND tp.dataVencimento < CURRENT_DATE " +
                                "AND tp.status NOT IN ('QUITADO','CANCELADO') " +
                                "ORDER BY tp.dataVencimento")
                .setParameter("empresaId", empresaId)
                .getResultList();

        LocalDate hoje = LocalDate.now();
        return rows.stream().map(r -> new RelatorioContasVencidasResponse(
                (String) r[0],
                (String) r[1],
                (String) r[2],
                (BigDecimal) r[3],
                (BigDecimal) r[4],
                (LocalDate) r[5],
                ChronoUnit.DAYS.between((LocalDate) r[5], hoje)
        )).toList();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RelatorioFluxoCaixaResponse> fluxoCaixa(Long empresaId, LocalDate inicio, LocalDate fim) {

        List<Object[]> rows = em.createQuery(
                        "SELECT fc.dataLancamento, " +
                                "COALESCE(SUM(CASE WHEN fc.tipo = 'ENTRADA' THEN fc.valor ELSE 0 END), 0), " +
                                "COALESCE(SUM(CASE WHEN fc.tipo = 'SAIDA' THEN fc.valor ELSE 0 END), 0) " +
                                "FROM FluxoCaixa fc " +
                                "WHERE fc.empresaId = :empresaId " +
                                "AND fc.dataLancamento BETWEEN :inicio AND :fim " +
                                "GROUP BY fc.dataLancamento " +
                                "ORDER BY fc.dataLancamento")
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        List<RelatorioFluxoCaixaResponse> resultado = new ArrayList<>();
        BigDecimal acumulado = BigDecimal.ZERO;

        for (Object[] r : rows) {
            BigDecimal entradas = (BigDecimal) r[1];
            BigDecimal saidas = (BigDecimal) r[2];
            BigDecimal saldo = entradas.subtract(saidas);
            acumulado = acumulado.add(saldo);

            resultado.add(new RelatorioFluxoCaixaResponse(
                    (LocalDate) r[0], entradas, saidas, saldo, acumulado
            ));
        }

        return resultado;
    }
}
