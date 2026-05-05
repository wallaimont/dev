package com.orionerp.modules.crm.dto;

import com.orionerp.modules.crm.domain.Lead;

import java.time.LocalDateTime;
import java.util.UUID;

public record LeadResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String nome,
        String email,
        String telefone,
        String empresaLead,
        String cargo,
        String origem,
        Long responsavelId,
        String status,
        Long convertidoClienteId,
        String observacao,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LeadResponse from(Lead l) {
        return new LeadResponse(
                l.getId(), l.getUuid(), l.getEmpresaId(),
                l.getNome(), l.getEmail(), l.getTelefone(),
                l.getEmpresaLead(), l.getCargo(), l.getOrigem(),
                l.getResponsavelId(), l.getStatus(),
                l.getConvertidoClienteId(), l.getObservacao(),
                l.getCreatedAt(), l.getUpdatedAt()
        );
    }
}
