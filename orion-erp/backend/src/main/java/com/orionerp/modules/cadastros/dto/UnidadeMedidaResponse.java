package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.UnidadeMedida;

import java.time.LocalDateTime;
import java.util.UUID;

public record UnidadeMedidaResponse(
        Long id,
        UUID uuid,
        String codigo,
        String nome,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UnidadeMedidaResponse from(UnidadeMedida e) {
        return new UnidadeMedidaResponse(
                e.getId(),
                e.getUuid(),
                e.getCodigo(),
                e.getNome(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
