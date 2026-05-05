package com.orionerp.modules.seguros.dto;

import java.math.BigDecimal;

public record CorretoraResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        String cnpj,
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
