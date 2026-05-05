package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Boleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BoletoRepository extends JpaRepository<Boleto, UUID> {
    Page<Boleto> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    List<Boleto> findByClienteIdAndEmpresaIdAndActiveTrue(UUID clienteId, UUID empresaId);
}
