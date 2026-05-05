package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransportadoraRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String razaoSocial,
        String nomeFantasia,
        @NotBlank String cpfCnpj,
        String endereco,
        String cidade,
        String uf,
        String telefone,
        String email
) {}
