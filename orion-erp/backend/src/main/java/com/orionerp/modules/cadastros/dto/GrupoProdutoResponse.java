package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.GrupoProduto;

import java.time.LocalDateTime;

public record GrupoProdutoResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GrupoProdutoResponse from(GrupoProduto e) {
        return new GrupoProdutoResponse(
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
