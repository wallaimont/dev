package com.orionerp.modules.crm.dto;

import com.orionerp.modules.crm.domain.AtividadeCrm;

import java.time.LocalDateTime;
import java.util.UUID;

public record AtividadeCrmResponse(
    Long id,
    UUID uuid,
    Long empresaId,
    String tipo,
    String titulo,
    String descricao,
    Long leadId,
    Long oportunidadeId,
    Long clienteId,
    Long responsavelId,
    LocalDateTime dataHora,
    Integer duracaoMinutos,
    Boolean concluida,
    LocalDateTime createdAt,
    String createdBy
) {
    public static AtividadeCrmResponse from(AtividadeCrm e) {
        return new AtividadeCrmResponse(
            e.getId(), e.getUuid(), e.getEmpresaId(), e.getTipo(), e.getTitulo(), e.getDescricao(),
            e.getLeadId(), e.getOportunidadeId(), e.getClienteId(), e.getResponsavelId(),
            e.getDataHora(), e.getDuracaoMinutos(), e.getConcluida(),
            e.getCreatedAt(), e.getCreatedBy());
    }
}
