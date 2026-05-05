package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Sinistro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SinistroRepository extends JpaRepository<Sinistro, UUID> {
    Page<Sinistro> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Optional<Sinistro> findByIdAndEmpresaId(UUID id, UUID empresaId);
    List<Sinistro> findByClienteIdAndEmpresaIdAndActiveTrue(UUID clienteId, UUID empresaId);
}
