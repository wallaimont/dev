package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CondicaoPagamentoRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        String tipo
) {}
