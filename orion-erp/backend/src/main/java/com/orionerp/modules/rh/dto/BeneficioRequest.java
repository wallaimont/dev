package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BeneficioRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotBlank String tipo,
        BigDecimal valorEmpresa,
        BigDecimal valorFuncionario,
        Boolean descontoFolha
) {}
