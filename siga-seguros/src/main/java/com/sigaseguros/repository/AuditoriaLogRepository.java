package com.sigaseguros.repository;

import com.sigaseguros.entity.AuditoriaLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

    @Query("SELECT a FROM AuditoriaLog a WHERE 1=1 " +
           "AND (:entidade IS NULL OR a.entidade = :entidade) " +
           "AND (:usuario IS NULL OR LOWER(a.usuario) LIKE LOWER(CONCAT('%', :usuario, '%'))) " +
           "AND (:acao IS NULL OR a.acao = :acao) " +
           "AND (:dataInicio IS NULL OR a.dataHora >= :dataInicio) " +
           "AND (:dataFim IS NULL OR a.dataHora <= :dataFim)")
    Page<AuditoriaLog> findAllWithFilters(@Param("entidade") String entidade,
                                           @Param("usuario") String usuario,
                                           @Param("acao") String acao,
                                           @Param("dataInicio") LocalDateTime dataInicio,
                                           @Param("dataFim") LocalDateTime dataFim,
                                           Pageable pageable);

    Page<AuditoriaLog> findByEntidadeAndEntidadeIdOrderByDataHoraDesc(String entidade, Long entidadeId, Pageable pageable);
}
