package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;

public record UnidadeMedidaRequest(
        @NotBlank String codigo,
        @NotBlank String nome
) {}
