package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.Marca;

import java.time.LocalDateTime;

public record MarcaResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MarcaResponse from(Marca e) {
        return new MarcaResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getNome(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
