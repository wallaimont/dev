package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.NaturezaFinanceira;

import java.time.LocalDateTime;

public record NaturezaFinanceiraResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        String tipo,
        Long parentId,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NaturezaFinanceiraResponse from(NaturezaFinanceira e) {
        return new NaturezaFinanceiraResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getNome(),
                e.getTipo(),
                e.getParentId(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
