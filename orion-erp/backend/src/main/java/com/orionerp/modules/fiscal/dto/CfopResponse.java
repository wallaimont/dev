package com.orionerp.modules.fiscal.dto;

import com.orionerp.modules.fiscal.domain.Cfop;

import java.time.LocalDateTime;

public record CfopResponse(
    Long id,
    String codigo,
    String descricao,
    String tipo,
    Boolean ativo,
    LocalDateTime createdAt
) {
    public static CfopResponse from(Cfop e) {
        return new CfopResponse(
            e.getId(), e.getCodigo(), e.getDescricao(),
            e.getTipo(), e.getAtivo(), e.getCreatedAt());
    }
}
