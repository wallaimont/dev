package com.sigaseguros.repository;

import com.sigaseguros.entity.Comissao;
import com.sigaseguros.enums.StatusComissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ComissaoRepository extends JpaRepository<Comissao, Long> {

    Optional<Comissao> findByIdAndActiveTrue(Long id);

    @Query("SELECT c FROM Comissao c WHERE c.active = true " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:seguradoraId IS NULL OR c.seguradora.id = :seguradoraId) " +
           "AND (:corretoraId IS NULL OR c.corretora.id = :corretoraId)")
    Page<Comissao> findAllWithFilters(@Param("status") StatusComissao status,
                                      @Param("seguradoraId") Long seguradoraId,
                                      @Param("corretoraId") Long corretoraId,
                                      Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Comissao c WHERE c.active = true AND c.status = :status")
    BigDecimal sumByStatus(@Param("status") StatusComissao status);

    @Query("SELECT MONTH(c.dataPrevista), SUM(c.valor) FROM Comissao c " +
           "WHERE c.active = true AND YEAR(c.dataPrevista) = :ano " +
           "GROUP BY MONTH(c.dataPrevista) ORDER BY MONTH(c.dataPrevista)")
    List<Object[]> comissoesMensal(@Param("ano") int ano);
}
