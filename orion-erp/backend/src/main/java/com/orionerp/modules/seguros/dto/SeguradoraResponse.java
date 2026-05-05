package com.orionerp.modules.seguros.dto;

public record SeguradoraResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        String cnpj,
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
