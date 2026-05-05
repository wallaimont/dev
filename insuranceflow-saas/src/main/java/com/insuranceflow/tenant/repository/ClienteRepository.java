package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Page<Cliente> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Optional<Cliente> findByIdAndEmpresaId(UUID id, UUID empresaId);
    long countByEmpresaIdAndActiveTrue(UUID empresaId);
    boolean existsByCpfCnpj(String cpfCnpj);
}
