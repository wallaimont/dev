package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Apolice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApoliceRepository extends JpaRepository<Apolice, UUID> {
    Page<Apolice> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Optional<Apolice> findByIdAndEmpresaId(UUID id, UUID empresaId);
    List<Apolice> findByClienteIdAndEmpresaIdAndActiveTrue(UUID clienteId, UUID empresaId);
    long countByEmpresaIdAndActiveTrue(UUID empresaId);
    long countByActiveTrue();
}
