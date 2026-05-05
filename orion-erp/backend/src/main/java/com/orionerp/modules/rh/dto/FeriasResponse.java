package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.Ferias;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FeriasResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long funcionarioId,
        LocalDate periodoAquisitivoInicio,
        LocalDate periodoAquisitivoFim,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer diasGozo,
        Integer diasAbono,
        BigDecimal valorFerias,
        BigDecimal valorAbono,
        BigDecimal valorAdiantamento13,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FeriasResponse from(Ferias f) {
        return new FeriasResponse(
                f.getId(), f.getUuid(), f.getEmpresaId(), f.getFilialId(),
                f.getFuncionarioId(), f.getPeriodoAquisitivoInicio(), f.getPeriodoAquisitivoFim(),
                f.getDataInicio(), f.getDataFim(), f.getDiasGozo(), f.getDiasAbono(),
                f.getValorFerias(), f.getValorAbono(), f.getValorAdiantamento13(),
                f.getStatus(), f.getCreatedAt(), f.getUpdatedAt()
        );
    }
}
