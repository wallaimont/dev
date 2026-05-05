package com.orionerp.modules.pcp.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApontamentoProducaoRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long ordemProducaoId,
        Long funcionarioId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        BigDecimal quantidadeProduzida,
        BigDecimal quantidadeRejeitada,
        String maquina,
        String observacao
) {}
