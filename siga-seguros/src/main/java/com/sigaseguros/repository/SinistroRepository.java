package com.sigaseguros.repository;

import com.sigaseguros.entity.Sinistro;
import com.sigaseguros.enums.StatusSinistro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SinistroRepository extends JpaRepository<Sinistro, Long> {

    Optional<Sinistro> findByIdAndActiveTrue(Long id);

    boolean existsByNumeroSinistroAndActiveTrue(String numeroSinistro);

    @Query("SELECT s FROM Sinistro s WHERE s.active = true " +
           "AND (:clienteId IS NULL OR s.cliente.id = :clienteId) " +
           "AND (:seguradoraId IS NULL OR s.seguradora.id = :seguradoraId) " +
           "AND (:status IS NULL OR s.status = :status)")
    Page<Sinistro> findAllWithFilters(@Param("clienteId") Long clienteId,
                                      @Param("seguradoraId") Long seguradoraId,
                                      @Param("status") StatusSinistro status,
                                      Pageable pageable);

    long countByStatusAndActiveTrue(StatusSinistro status);

    long countByActiveTrue();

    List<Sinistro> findTop10ByActiveTrueAndStatusInOrderByDataAvisoDesc(List<StatusSinistro> statuses);

    @Query("SELECT s.status, COUNT(s) FROM Sinistro s WHERE s.active = true GROUP BY s.status")
    List<Object[]> countByStatus();
}
