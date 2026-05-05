package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;

public record BancoRequest(
        @NotBlank String codigoBanco,
        @NotBlank String nome
) {}
