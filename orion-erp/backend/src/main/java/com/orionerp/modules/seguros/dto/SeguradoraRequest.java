package com.orionerp.modules.seguros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeguradoraRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotBlank String cnpj,
        String registroSusep,
        String email,
        String telefone,
        String endereco,
        String cidade,
        String uf,
        String cep,
        String contato,
        String observacao,
        Boolean ativo
) {}
