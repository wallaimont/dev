package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ClienteRequest {
    @NotBlank private String tipoPessoa;
    @NotBlank private String nome;
    private String cpfCnpj;
    private String rg;
    private LocalDate dataNascimento;
    private String sexo;
    private String estadoCivil;
    private String profissao;
    @Email private String email;
    private String telefone;
    private String celular;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String observacoes;
    private String origem;
}
