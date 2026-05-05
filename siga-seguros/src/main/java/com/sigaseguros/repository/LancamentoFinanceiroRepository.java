package com.sigaseguros.repository;

import com.sigaseguros.entity.LancamentoFinanceiro;
import com.sigaseguros.enums.StatusFinanceiro;
import com.sigaseguros.enums.TipoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, Long> {

    Optional<LancamentoFinanceiro> findByIdAndActiveTrue(Long id);

    @Query("SELECT l FROM LancamentoFinanceiro l WHERE l.active = true " +
           "AND (:tipo IS NULL OR l.tipoLancamento = :tipo) " +
           "AND (:status IS NULL OR l.status = :status) " +
           "AND (:dataInicio IS NULL OR l.vencimento >= :dataInicio) " +
           "AND (:dataFim IS NULL OR l.vencimento <= :dataFim)")
    Page<LancamentoFinanceiro> findAllWithFilters(@Param("tipo") TipoLancamento tipo,
                                                   @Param("status") StatusFinanceiro status,
                                                   @Param("dataInicio") LocalDate dataInicio,
                                                   @Param("dataFim") LocalDate dataFim,
                                                   Pageable pageable);

    @Query("SELECT COALESCE(SUM(l.valor), 0) FROM LancamentoFinanceiro l WHERE l.active = true " +
           "AND l.tipoLancamento = :tipo AND l.status = :status")
    BigDecimal sumByTipoAndStatus(@Param("tipo") TipoLancamento tipo, @Param("status") StatusFinanceiro status);

    long countByStatusAndActiveTrueAndTipoLancamento(StatusFinanceiro status, TipoLancamento tipo);

    @Query("SELECT MONTH(l.vencimento), SUM(l.valor) FROM LancamentoFinanceiro l " +
           "WHERE l.active = true AND l.tipoLancamento = 'RECEITA' AND YEAR(l.vencimento) = :ano " +
           "GROUP BY MONTH(l.vencimento) ORDER BY MONTH(l.vencimento)")
    List<Object[]> faturamentoMensal(@Param("ano") int ano);
}
