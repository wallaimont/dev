package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.AssinaturaEmpresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssinaturaEmpresaRepository extends JpaRepository<AssinaturaEmpresa, UUID> {
    Optional<AssinaturaEmpresa> findByEmpresaIdAndActiveTrue(UUID empresaId);
    Page<AssinaturaEmpresa> findByActiveTrue(Pageable pageable);
    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(a.valor), 0) FROM AssinaturaEmpresa a WHERE a.status = 'ATIVA' AND a.ciclo = 'MENSAL' AND a.active = true")
    BigDecimal calcularMRR();

    @Query("SELECT COALESCE(SUM(a.valor / 12), 0) FROM AssinaturaEmpresa a WHERE a.status = 'ATIVA' AND a.ciclo = 'ANUAL' AND a.active = true")
    BigDecimal calcularMRRAnual();

    List<AssinaturaEmpresa> findByStatusAndDataVencimentoBefore(String status, LocalDateTime data);

    List<AssinaturaEmpresa> findByStatusIn(List<String> statuses);

    @Query("SELECT a FROM AssinaturaEmpresa a WHERE a.status = 'TRIAL' AND a.dataVencimento < :agora AND a.active = true")
    List<AssinaturaEmpresa> findTrialsExpirados(LocalDateTime agora);

    @Query("SELECT a FROM AssinaturaEmpresa a WHERE a.status = 'ATIVA' AND a.dataVencimento <= :data AND a.active = true")
    List<AssinaturaEmpresa> findAtivasVencendo(LocalDateTime data);

    Page<AssinaturaEmpresa> findByStatusAndActiveTrue(String status, Pageable pageable);
}
