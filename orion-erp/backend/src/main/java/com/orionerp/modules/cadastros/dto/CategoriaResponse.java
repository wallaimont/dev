package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.Categoria;

import java.time.LocalDateTime;

public record CategoriaResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        Long parentId,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CategoriaResponse from(Categoria e) {
        return new CategoriaResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getNome(),
                e.getParentId(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
