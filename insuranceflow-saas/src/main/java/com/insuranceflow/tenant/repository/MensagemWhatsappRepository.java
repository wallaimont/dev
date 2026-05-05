package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.MensagemWhatsapp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MensagemWhatsappRepository extends JpaRepository<MensagemWhatsapp, UUID> {
    Page<MensagemWhatsapp> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    List<MensagemWhatsapp> findByClienteIdAndEmpresaIdAndActiveTrue(UUID clienteId, UUID empresaId);
    Optional<MensagemWhatsapp> findByIdAndEmpresaId(UUID id, UUID empresaId);
    long countByEmpresaIdAndActiveTrue(UUID empresaId);
}
