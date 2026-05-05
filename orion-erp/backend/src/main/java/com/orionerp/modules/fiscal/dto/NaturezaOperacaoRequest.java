package com.orionerp.modules.fiscal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NaturezaOperacaoRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotBlank @Pattern(regexp = "ENTRADA|SAIDA") String tipo,
        Long cfopId,
        Boolean geraFinanceiro,
        Boolean movimentaEstoque
) {
}
