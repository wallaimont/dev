package com.orionerp.modules.relatorios.service;

import com.orionerp.modules.relatorios.dto.RelatorioAniversariantesResponse;
import com.orionerp.modules.relatorios.dto.RelatorioFolhaPagamentoResponse;
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
public class RelatorioRhService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public RelatorioFolhaPagamentoResponse resumoFolha(Long empresaId, Integer ano, Integer mes) {

        Object[] totais = em.createQuery(
                        "SELECT COALESCE(SUM(fp.totalProventos), 0), " +
                                "COALESCE(SUM(fp.totalDescontos), 0), " +
                                "COALESCE(SUM(fp.totalLiquido), 0), " +
                                "COALESCE(SUM(fp.totalEncargos), 0) " +
                                "FROM FolhaPagamento fp " +
                                "WHERE fp.empresaId = :empresaId AND fp.deleted = false " +
                                "AND fp.ano = :ano AND fp.mes = :mes",
                        Object[].class)
                .setParameter("empresaId", empresaId)
                .setParameter("ano", ano)
                .setParameter("mes", mes)
                .getSingleResult();

        Long totalFunc = em.createQuery(
                        "SELECT COUNT(DISTINCT fpi.funcionarioId) " +
                                "FROM FolhaPagamentoItem fpi " +
                                "WHERE fpi.deleted = false AND fpi.folhaPagamentoId IN " +
                                "(SELECT fp.id FROM FolhaPagamento fp " +
                                " WHERE fp.empresaId = :empresaId AND fp.deleted = false " +
                                " AND fp.ano = :ano AND fp.mes = :mes)",
                        Long.class)
                .setParameter("empresaId", empresaId)
                .setParameter("ano", ano)
                .setParameter("mes", mes)
                .getSingleResult();

        Object[] encargos = em.createQuery(
                        "SELECT COALESCE(SUM(fpi.fgts), 0), COALESCE(SUM(fpi.inssEmpresa), 0) " +
                                "FROM FolhaPagamentoItem fpi " +
                                "WHERE fpi.deleted = false AND fpi.folhaPagamentoId IN " +
                                "(SELECT fp.id FROM FolhaPagamento fp " +
                                " WHERE fp.empresaId = :empresaId AND fp.deleted = false " +
                                " AND fp.ano = :ano AND fp.mes = :mes)",
                        Object[].class)
                .setParameter("empresaId", empresaId)
                .setParameter("ano", ano)
                .setParameter("mes", mes)
                .getSingleResult();

        String competencia = String.format("%02d/%d", mes, ano);

        return new RelatorioFolhaPagamentoResponse(
                competencia,
                totalFunc,
                (BigDecimal) totais[0],
                (BigDecimal) totais[1],
                (BigDecimal) totais[2],
                (BigDecimal) encargos[0],
                (BigDecimal) encargos[1]
        );
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<RelatorioAniversariantesResponse> aniversariantes(Long empresaId, Integer mes) {

        List<Object[]> rows = em.createQuery(
                        "SELECT f.nome, " +
                                "(SELECT d.nome FROM DepartamentoRh d WHERE d.id = f.departamentoId), " +
                                "(SELECT c.nome FROM Cargo c WHERE c.id = f.cargoId), " +
                                "f.dataNascimento, " +
                                "EXTRACT(DAY FROM f.dataNascimento) " +
                                "FROM Funcionario f " +
                                "WHERE f.empresaId = :empresaId AND f.deleted = false " +
                                "AND f.situacao = 'ATIVO' " +
                                "AND EXTRACT(MONTH FROM f.dataNascimento) = :mes " +
                                "ORDER BY EXTRACT(DAY FROM f.dataNascimento)")
                .setParameter("empresaId", empresaId)
                .setParameter("mes", mes)
                .getResultList();

        return rows.stream().map(r -> new RelatorioAniversariantesResponse(
                (String) r[0],
                r[1] != null ? (String) r[1] : "",
                r[2] != null ? (String) r[2] : "",
                (LocalDate) r[3],
                r[4] != null ? ((Number) r[4]).intValue() : null
        )).toList();
    }
}
