package com.orionerp.modules.administration.dto;

import java.util.UUID;

public record EmpresaResponse(
        Long id,
        UUID uuid,
        String codigo,
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String email,
        String cidade,
        String uf,
        Boolean ativo,
        String observacao
) {
}
