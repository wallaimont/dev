package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.Cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteResponse(
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
        String celular,
        String email,
        String website,
        BigDecimal limiteCredito,
        BigDecimal saldoDevedor,
        Boolean bloqueioFinanceiro,
        Boolean bloqueioComercial,
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ClienteResponse from(Cliente e) {
        return new ClienteResponse(
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
                e.getCelular(),
                e.getEmail(),
                e.getWebsite(),
                e.getLimiteCredito(),
                e.getSaldoDevedor(),
                e.getBloqueioFinanceiro(),
                e.getBloqueioComercial(),
                e.getObservacao(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
