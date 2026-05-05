package com.sigaseguros.repository;

import com.sigaseguros.entity.Apolice;
import com.sigaseguros.enums.StatusApolice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApoliceRepository extends JpaRepository<Apolice, Long> {

    Optional<Apolice> findByIdAndActiveTrue(Long id);

    boolean existsByNumeroApoliceAndActiveTrue(String numeroApolice);

    @Query("SELECT a FROM Apolice a WHERE a.active = true " +
           "AND (:clienteId IS NULL OR a.cliente.id = :clienteId) " +
           "AND (:seguradoraId IS NULL OR a.seguradora.id = :seguradoraId) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:ramoId IS NULL OR a.ramoSeguro.id = :ramoId)")
    Page<Apolice> findAllWithFilters(@Param("clienteId") Long clienteId,
                                     @Param("seguradoraId") Long seguradoraId,
                                     @Param("status") StatusApolice status,
                                     @Param("ramoId") Long ramoId,
                                     Pageable pageable);

    long countByStatusAndActiveTrue(StatusApolice status);

    long countByActiveTrue();

    @Query("SELECT a FROM Apolice a WHERE a.active = true AND a.status = 'ATIVA' " +
           "AND a.fimVigencia BETWEEN :inicio AND :fim ORDER BY a.fimVigencia ASC")
    List<Apolice> findVencendoEntre(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    List<Apolice> findTop10ByActiveTrueAndStatusOrderByFimVigenciaAsc(StatusApolice status);

    @Query("SELECT a.seguradora.nome, COUNT(a) FROM Apolice a WHERE a.active = true AND a.status = 'ATIVA' GROUP BY a.seguradora.nome")
    List<Object[]> countBySeguradora();

    @Query("SELECT a FROM Apolice a WHERE a.active = true AND a.status = 'ATIVA' " +
           "AND a.fimVigencia <= :data")
    List<Apolice> findVencidas(@Param("data") LocalDate data);
}
