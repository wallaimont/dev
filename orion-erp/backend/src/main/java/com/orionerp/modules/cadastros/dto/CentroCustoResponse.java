package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.CentroCusto;

import java.time.LocalDateTime;

public record CentroCustoResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        Long parentId,
        Integer nivel,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CentroCustoResponse from(CentroCusto e) {
        return new CentroCustoResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getNome(),
                e.getParentId(),
                e.getNivel(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
