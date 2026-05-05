package com.orionerp.modules.contabilidade.dto;

import com.orionerp.modules.contabilidade.domain.CentroResultado;

import java.time.LocalDateTime;
import java.util.UUID;

public record CentroResultadoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String descricao,
        String tipo,
        String responsavel,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CentroResultadoResponse from(CentroResultado e) {
        return new CentroResultadoResponse(
                e.getId(), e.getUuid(), e.getEmpresaId(),
                e.getCodigo(), e.getDescricao(), e.getTipo(),
                e.getResponsavel(), e.getAtivo(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
