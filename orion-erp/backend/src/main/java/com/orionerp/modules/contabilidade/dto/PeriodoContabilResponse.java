package com.orionerp.modules.contabilidade.dto;

import com.orionerp.modules.contabilidade.domain.PeriodoContabil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PeriodoContabilResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Integer ano,
        Integer mes,
        LocalDate dataInicio,
        LocalDate dataFim,
        String status,
        String fechadoPor,
        LocalDateTime fechadoEm,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PeriodoContabilResponse from(PeriodoContabil e) {
        return new PeriodoContabilResponse(
                e.getId(), e.getUuid(), e.getEmpresaId(),
                e.getAno(), e.getMes(), e.getDataInicio(), e.getDataFim(),
                e.getStatus(), e.getFechadoPor(), e.getFechadoEm(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
