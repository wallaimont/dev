package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CargoRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        String cbo,
        BigDecimal salarioBase,
        String nivel
) {}
