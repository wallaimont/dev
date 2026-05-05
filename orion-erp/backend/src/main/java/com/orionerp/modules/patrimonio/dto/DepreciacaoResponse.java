package com.orionerp.modules.patrimonio.dto;

import com.orionerp.modules.patrimonio.domain.Depreciacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DepreciacaoResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long bemPatrimonialId,
        LocalDate dataDepreciacao,
        BigDecimal valorDepreciacao,
        BigDecimal valorAcumulado,
        BigDecimal valorLiquido,
        Integer mesReferencia,
        Integer anoReferencia,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DepreciacaoResponse from(Depreciacao e) {
        return new DepreciacaoResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getBemPatrimonialId(), e.getDataDepreciacao(),
                e.getValorDepreciacao(), e.getValorAcumulado(), e.getValorLiquido(),
                e.getMesReferencia(), e.getAnoReferencia(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
