package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.SubgrupoProduto;

import java.time.LocalDateTime;

public record SubgrupoProdutoResponse(
        Long id,
        Long empresaId,
        Long grupoId,
        String codigo,
        String nome,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SubgrupoProdutoResponse from(SubgrupoProduto e) {
        return new SubgrupoProdutoResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getGrupo() != null ? e.getGrupo().getId() : null,
                e.getCodigo(),
                e.getNome(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
