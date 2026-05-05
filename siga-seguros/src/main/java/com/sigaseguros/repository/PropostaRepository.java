package com.sigaseguros.repository;

import com.sigaseguros.entity.Proposta;
import com.sigaseguros.enums.StatusProposta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    Optional<Proposta> findByIdAndActiveTrue(Long id);

    boolean existsByNumeroPropostaAndActiveTrue(String numeroProposta);

    @Query("SELECT p FROM Proposta p WHERE p.active = true " +
           "AND (:clienteId IS NULL OR p.cliente.id = :clienteId) " +
           "AND (:seguradoraId IS NULL OR p.seguradora.id = :seguradoraId) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:dataInicio IS NULL OR p.createdAt >= :dataInicio) " +
           "AND (:dataFim IS NULL OR p.createdAt <= :dataFim)")
    Page<Proposta> findAllWithFilters(@Param("clienteId") Long clienteId,
                                      @Param("seguradoraId") Long seguradoraId,
                                      @Param("status") StatusProposta status,
                                      @Param("dataInicio") LocalDate dataInicio,
                                      @Param("dataFim") LocalDate dataFim,
                                      Pageable pageable);

    long countByStatusAndActiveTrue(StatusProposta status);

    long countByActiveTrue();

    List<Proposta> findTop10ByActiveTrueOrderByCreatedAtDesc();

    @Query("SELECT p.status, COUNT(p) FROM Proposta p WHERE p.active = true GROUP BY p.status")
    List<Object[]> countByStatus();
}
