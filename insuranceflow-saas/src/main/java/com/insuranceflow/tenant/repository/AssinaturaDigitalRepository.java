package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.AssinaturaDigital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssinaturaDigitalRepository extends JpaRepository<AssinaturaDigital, UUID> {
    Page<AssinaturaDigital> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    List<AssinaturaDigital> findByEntidadeTipoAndEntidadeIdAndEmpresaIdAndActiveTrue(String entidadeTipo, UUID entidadeId, UUID empresaId);
    Optional<AssinaturaDigital> findByIdAndEmpresaId(UUID id, UUID empresaId);
    long countByEmpresaIdAndStatusAndActiveTrue(UUID empresaId, String status);
    List<AssinaturaDigital> findBySignatarioEmailAndEmpresaIdAndActiveTrue(String signatarioEmail, UUID empresaId);
}
