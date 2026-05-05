package com.orionerp.modules.fiscal.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record NcmRequest(
    @NotBlank String codigo,
    @NotBlank String descricao,
    BigDecimal aliquotaIpi,
    Boolean ativo
) {}
