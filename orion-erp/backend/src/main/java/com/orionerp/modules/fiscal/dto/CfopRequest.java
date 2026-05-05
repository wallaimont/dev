package com.orionerp.modules.fiscal.dto;

import jakarta.validation.constraints.NotBlank;

public record CfopRequest(
    @NotBlank String codigo,
    @NotBlank String descricao,
    @NotBlank String tipo,
    Boolean ativo
) {}
