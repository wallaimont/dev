package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {
    Page<Auditoria> findByEmpresaIdOrderByCreatedAtDesc(UUID empresaId, Pageable pageable);
    Page<Auditoria> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
