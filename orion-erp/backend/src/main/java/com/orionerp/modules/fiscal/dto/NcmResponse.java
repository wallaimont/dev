package com.orionerp.modules.fiscal.dto;

import com.orionerp.modules.fiscal.domain.Ncm;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NcmResponse(
    Long id,
    String codigo,
    String descricao,
    BigDecimal aliquotaIpi,
    Boolean ativo,
    LocalDateTime createdAt
) {
    public static NcmResponse from(Ncm e) {
        return new NcmResponse(
            e.getId(), e.getCodigo(), e.getDescricao(),
            e.getAliquotaIpi(), e.getAtivo(), e.getCreatedAt());
    }
}
