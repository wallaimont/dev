package com.orionerp.modules.estoque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocalizacaoRequest(
    @NotNull Long armazemId,
    @NotBlank String codigo,
    String descricao,
    Boolean ativo
) {}
