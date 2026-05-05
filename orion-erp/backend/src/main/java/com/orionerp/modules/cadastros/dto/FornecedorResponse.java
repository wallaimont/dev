package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.Fornecedor;

import java.time.LocalDateTime;

public record FornecedorResponse(
        Long id,
        Long empresaId,
        String codigo,
        String tipoPessoa,
        String razaoSocial,
        String nomeFantasia,
        String cpfCnpj,
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
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FornecedorResponse from(Fornecedor e) {
        return new FornecedorResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getTipoPessoa(),
                e.getRazaoSocial(),
                e.getNomeFantasia(),
                e.getCpfCnpj(),
                e.getInscricaoEstadual(),
                e.getEndereco(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getUf(),
                e.getCep(),
                e.getTelefone(),
                e.getEmail(),
                e.getWebsite(),
                e.getObservacao(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
