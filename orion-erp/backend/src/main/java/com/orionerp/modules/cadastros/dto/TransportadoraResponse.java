package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.Transportadora;

import java.time.LocalDateTime;

public record TransportadoraResponse(
        Long id,
        Long empresaId,
        String codigo,
        String razaoSocial,
        String nomeFantasia,
        String cpfCnpj,
        String endereco,
        String cidade,
        String uf,
        String telefone,
        String email,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransportadoraResponse from(Transportadora e) {
        return new TransportadoraResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getRazaoSocial(),
                e.getNomeFantasia(),
                e.getCpfCnpj(),
                e.getEndereco(),
                e.getCidade(),
                e.getUf(),
                e.getTelefone(),
                e.getEmail(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
