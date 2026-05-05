package com.orionerp.modules.relatorios.service;

import com.orionerp.modules.relatorios.dto.RelatorioDreSimplificadoResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RelatorioContabilService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public RelatorioDreSimplificadoResponse dreSimplificado(Long empresaId, LocalDate inicio, LocalDate fim) {

        // Receita Bruta — créditos em contas do grupo 3 (Receitas)
        BigDecimal receitaBruta = somaLancamentosPorCredito(empresaId, inicio, fim, "3%");

        // Deduções sobre receita — débitos em contas do subgrupo 3.1 (Deduções de Receita)
        BigDecimal deducoes = somaLancamentosPorDebito(empresaId, inicio, fim, "3.1%");

        BigDecimal receitaLiquida = receitaBruta.subtract(deducoes);

        // CMV / CPV — débitos em contas do grupo 4.1 (Custos)
        BigDecimal custos = somaLancamentosPorDebito(empresaId, inicio, fim, "4.1%");

        BigDecimal lucroBruto = receitaLiquida.subtract(custos);

        // Despesas Operacionais — débitos em contas do grupo 4.2 (Despesas)
        BigDecimal despesasOperacionais = somaLancamentosPorDebito(empresaId, inicio, fim, "4.2%");

        BigDecimal resultadoOperacional = lucroBruto.subtract(despesasOperacionais);

        // Resultado Financeiro — créditos em 3.2 (Receitas Financeiras) menos débitos em 4.3 (Desp. Financeiras)
        BigDecimal receitasFinanceiras = somaLancamentosPorCredito(empresaId, inicio, fim, "3.2%");
        BigDecimal despesasFinanceiras = somaLancamentosPorDebito(empresaId, inicio, fim, "4.3%");
        BigDecimal resultadoFinanceiro = receitasFinanceiras.subtract(despesasFinanceiras);

        BigDecimal resultadoAntesIr = resultadoOperacional.add(resultadoFinanceiro);

        // Provisão IR/CSLL — débitos em contas do grupo 4.4
        BigDecimal provisaoIr = somaLancamentosPorDebito(empresaId, inicio, fim, "4.4%");

        BigDecimal lucroLiquido = resultadoAntesIr.subtract(provisaoIr);

        String periodo = inicio + " a " + fim;

        return new RelatorioDreSimplificadoResponse(
                periodo,
                receitaBruta,
                deducoes,
                receitaLiquida,
                custos,
                lucroBruto,
                despesasOperacionais,
                resultadoOperacional,
                resultadoFinanceiro,
                resultadoAntesIr,
                provisaoIr,
                lucroLiquido
        );
    }

    private BigDecimal somaLancamentosPorCredito(Long empresaId, LocalDate inicio, LocalDate fim, String classificacaoLike) {
        return em.createQuery(
                        "SELECT COALESCE(SUM(lc.valor), 0) FROM LancamentoContabil lc " +
                                "WHERE lc.empresaId = :empresaId AND lc.deleted = false " +
                                "AND lc.dataLancamento BETWEEN :inicio AND :fim " +
                                "AND lc.status <> 'ESTORNADO' " +
                                "AND lc.contaCreditoId IN " +
                                "(SELECT pc.id FROM PlanoContas pc WHERE pc.empresaId = :empresaId AND pc.classificacao LIKE :classificacao)",
                        BigDecimal.class)
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setParameter("classificacao", classificacaoLike)
                .getSingleResult();
    }

    private BigDecimal somaLancamentosPorDebito(Long empresaId, LocalDate inicio, LocalDate fim, String classificacaoLike) {
        return em.createQuery(
                        "SELECT COALESCE(SUM(lc.valor), 0) FROM LancamentoContabil lc " +
                                "WHERE lc.empresaId = :empresaId AND lc.deleted = false " +
                                "AND lc.dataLancamento BETWEEN :inicio AND :fim " +
                                "AND lc.status <> 'ESTORNADO' " +
                                "AND lc.contaDebitoId IN " +
                                "(SELECT pc.id FROM PlanoContas pc WHERE pc.empresaId = :empresaId AND pc.classificacao LIKE :classificacao)",
                        BigDecimal.class)
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setParameter("classificacao", classificacaoLike)
                .getSingleResult();
    }
}
