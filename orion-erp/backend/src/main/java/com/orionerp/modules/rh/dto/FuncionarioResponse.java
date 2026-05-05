package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.Funcionario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FuncionarioResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        String codigo,
        String nome,
        String cpf,
        String rg,
        LocalDate dataNascimento,
        String sexo,
        String estadoCivil,
        String endereco,
        String cidade,
        String uf,
        String cep,
        String telefone,
        String email,
        Long departamentoId,
        Long cargoId,
        LocalDate dataAdmissao,
        LocalDate dataDemissao,
        BigDecimal salario,
        String situacao,
        Long usuarioId,
        String pis,
        String ctps,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FuncionarioResponse from(Funcionario f) {
        return new FuncionarioResponse(
                f.getId(), f.getUuid(), f.getEmpresaId(), f.getFilialId(),
                f.getCodigo(), f.getNome(), f.getCpf(), f.getRg(),
                f.getDataNascimento(), f.getSexo(), f.getEstadoCivil(),
                f.getEndereco(), f.getCidade(), f.getUf(), f.getCep(),
                f.getTelefone(), f.getEmail(),
                f.getDepartamentoId(), f.getCargoId(),
                f.getDataAdmissao(), f.getDataDemissao(),
                f.getSalario(), f.getSituacao(), f.getUsuarioId(),
                f.getPis(), f.getCtps(),
                f.getAtivo(), f.getCreatedAt(), f.getUpdatedAt()
        );
    }
}
