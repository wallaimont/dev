package com.sigaseguros.dto;

import com.sigaseguros.enums.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDTO {

    private Long id;

    @NotNull(message = "Tipo de pessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @Size(max = 150)
    private String nome;

    @Size(max = 200)
    private String razaoSocial;

    @Size(max = 200)
    private String nomeFantasia;

    private String cpf;
    private String cnpj;
    private String rg;
    private String inscricaoEstadual;
    private String dataNascimento;
    private String dataFundacao;

    @Size(max = 20)
    private String telefone;

    @Size(max = 20)
    private String celular;

    @Email(message = "E-mail inválido")
    @Size(max = 200)
    private String email;

    @Size(max = 10)
    private String cep;

    @Size(max = 200)
    private String logradouro;

    @Size(max = 10)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @Size(max = 100)
    private String bairro;

    @Size(max = 100)
    private String cidade;

    @Size(max = 2)
    private String estado;

    private String observacoes;
    private Boolean active;
    private String nomeExibicao;
    private String createdAt;
}
