package com.orionerp.modules.contratos.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoParcelaRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long contratoId,
        @NotNull Integer numeroParcela,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valor,
        BigDecimal valorPago,
        String status
) {}
