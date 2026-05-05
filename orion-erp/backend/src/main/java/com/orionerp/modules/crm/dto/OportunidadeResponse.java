package com.orionerp.modules.crm.dto;

import com.orionerp.modules.crm.domain.Oportunidade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record OportunidadeResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String titulo,
        Long leadId,
        String leadNome,
        Long clienteId,
        Long responsavelId,
        BigDecimal valorEstimado,
        Integer probabilidade,
        String etapaFunil,
        LocalDate dataPrevisaoFechamento,
        String motivoPerda,
        String observacao,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OportunidadeResponse from(Oportunidade o) {
        return new OportunidadeResponse(
                o.getId(), o.getUuid(), o.getEmpresaId(),
                o.getTitulo(),
                o.getLead() != null ? o.getLead().getId() : null,
                o.getLead() != null ? o.getLead().getNome() : null,
                o.getClienteId(), o.getResponsavelId(),
                o.getValorEstimado(), o.getProbabilidade(),
                o.getEtapaFunil(), o.getDataPrevisaoFechamento(),
                o.getMotivoPerda(), o.getObservacao(),
                o.getCreatedAt(), o.getUpdatedAt()
        );
    }
}
