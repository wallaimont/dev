package com.orionerp.modules.fiscal.dto;

import com.orionerp.modules.fiscal.domain.Cst;

import java.time.LocalDateTime;

public record CstResponse(
    Long id,
    String codigo,
    String descricao,
    String tipoImposto,
    Boolean ativo,
    LocalDateTime createdAt
) {
    public static CstResponse from(Cst e) {
        return new CstResponse(
            e.getId(), e.getCodigo(), e.getDescricao(),
            e.getTipoImposto(), e.getAtivo(), e.getCreatedAt());
    }
}
