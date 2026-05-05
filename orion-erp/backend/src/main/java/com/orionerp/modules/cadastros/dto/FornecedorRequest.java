package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FornecedorRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        String tipoPessoa,
        @NotBlank String razaoSocial,
        String nomeFantasia,
        @NotBlank String cpfCnpj,
        String inscricaoEstadual,
        String endereco,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        String cep,
        String telefone,
        String email,
        String website,
        String observacao
) {}
