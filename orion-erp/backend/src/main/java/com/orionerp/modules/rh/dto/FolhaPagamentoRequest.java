package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FolhaPagamentoRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Integer ano,
        @NotNull Integer mes,
        String tipo,
        LocalDate dataCalculo,
        LocalDate dataPagamento,
        BigDecimal totalProventos,
        BigDecimal totalDescontos,
        BigDecimal totalLiquido,
        BigDecimal totalEncargos
) {}
