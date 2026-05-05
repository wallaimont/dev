package com.orionerp.modules.seguros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CorretoraRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotBlank String cnpj,
        String responsavel,
        String email,
        String telefone,
        String endereco,
        String cidade,
        String uf,
        String cep,
        BigDecimal percentualComissao,
        String observacao,
        Boolean ativo
) {}
