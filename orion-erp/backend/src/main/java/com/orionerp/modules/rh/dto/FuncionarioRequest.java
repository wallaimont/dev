package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FuncionarioRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotBlank String cpf,
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
        @NotNull LocalDate dataAdmissao,
        BigDecimal salario,
        String pis,
        String ctps,
        String observacao
) {
}
