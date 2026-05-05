package com.orionerp.modules.relatorios.service;

import com.orionerp.modules.relatorios.dto.RelatorioFaturamentoPeriodoResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RelatorioFiscalService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public RelatorioFaturamentoPeriodoResponse faturamentoPeriodo(Long empresaId, LocalDate inicio, LocalDate fim) {

        Object[] r = em.createQuery(
                        "SELECT COUNT(nf), " +
                                "COALESCE(SUM(nf.valorProdutos), 0), " +
                                "COALESCE(SUM(" +
                                "  COALESCE(nf.valorIcms, 0) + COALESCE(nf.valorIcmsSt, 0) + " +
                                "  COALESCE(nf.valorIpi, 0) + COALESCE(nf.valorPis, 0) + " +
                                "  COALESCE(nf.valorCofins, 0)" +
                                "), 0), " +
                                "COALESCE(SUM(nf.valorTotal), 0) " +
                                "FROM NotaFiscal nf " +
                                "WHERE nf.empresaId = :empresaId AND nf.deleted = false " +
                                "AND nf.dataEmissao BETWEEN :inicio AND :fim " +
                                "AND nf.status <> 'CANCELADA'",
                        Object[].class)
                .setParameter("empresaId", empresaId)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();

        String periodo = inicio + " a " + fim;

        return new RelatorioFaturamentoPeriodoResponse(
                periodo,
                (Long) r[0],
                (BigDecimal) r[1],
                (BigDecimal) r[2],
                (BigDecimal) r[3]
        );
    }
}
