package com.orionerp.modules.fiscal.dto;

import jakarta.validation.constraints.NotBlank;

public record CstRequest(
    @NotBlank String codigo,
    @NotBlank String descricao,
    @NotBlank String tipoImposto,
    Boolean ativo
) {}
