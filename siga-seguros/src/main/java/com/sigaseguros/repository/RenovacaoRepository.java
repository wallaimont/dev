package com.sigaseguros.repository;

import com.sigaseguros.entity.Renovacao;
import com.sigaseguros.enums.StatusRenovacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RenovacaoRepository extends JpaRepository<Renovacao, Long> {

    Optional<Renovacao> findByIdAndActiveTrue(Long id);

    @Query("SELECT r FROM Renovacao r WHERE r.active = true " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:clienteId IS NULL OR r.cliente.id = :clienteId)")
    Page<Renovacao> findAllWithFilters(@Param("status") StatusRenovacao status,
                                       @Param("clienteId") Long clienteId,
                                       Pageable pageable);

    long countByStatusAndActiveTrue(StatusRenovacao status);

    @Query("SELECT r.status, COUNT(r) FROM Renovacao r WHERE r.active = true GROUP BY r.status")
    List<Object[]> countByStatus();
}
