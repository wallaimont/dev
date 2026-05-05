package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.FuncionarioBeneficio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FuncionarioBeneficioResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long funcionarioId,
        Long beneficioId,
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal valorCustomizado,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FuncionarioBeneficioResponse from(FuncionarioBeneficio fb) {
        return new FuncionarioBeneficioResponse(
                fb.getId(), fb.getUuid(), fb.getEmpresaId(), fb.getFuncionarioId(),
                fb.getBeneficioId(), fb.getDataInicio(), fb.getDataFim(),
                fb.getValorCustomizado(), fb.getAtivo(), fb.getCreatedAt(), fb.getUpdatedAt()
        );
    }
}
