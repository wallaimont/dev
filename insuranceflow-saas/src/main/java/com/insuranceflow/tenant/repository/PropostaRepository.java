package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Proposta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropostaRepository extends JpaRepository<Proposta, UUID> {
    Page<Proposta> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Optional<Proposta> findByIdAndEmpresaId(UUID id, UUID empresaId);
    long countByEmpresaIdAndActiveTrue(UUID empresaId);
    long countByActiveTrue();
    List<Proposta> findByClienteIdAndEmpresaIdAndActiveTrue(UUID clienteId, UUID empresaId);
}
