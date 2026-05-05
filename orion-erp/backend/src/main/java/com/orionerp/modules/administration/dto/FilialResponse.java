package com.orionerp.modules.administration.dto;

import java.util.UUID;

public record FilialResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String empresaNome,
        String codigo,
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String email,
        String cidade,
        String uf,
        Boolean matriz,
        Boolean ativo,
        String observacao
) {
}
