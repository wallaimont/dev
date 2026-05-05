package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Corretora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CorretoraRepository extends JpaRepository<Corretora, UUID> {
    Page<Corretora> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Optional<Corretora> findByIdAndEmpresaId(UUID id, UUID empresaId);
}
