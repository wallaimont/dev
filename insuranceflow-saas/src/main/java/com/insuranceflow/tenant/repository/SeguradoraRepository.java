package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

// Seguradora
public interface SeguradoraRepository extends JpaRepository<Seguradora, UUID> {
    Page<Seguradora> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Optional<Seguradora> findByIdAndEmpresaId(UUID id, UUID empresaId);
}
