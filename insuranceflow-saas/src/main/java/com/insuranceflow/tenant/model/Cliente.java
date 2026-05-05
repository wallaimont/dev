package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente extends TenantBaseEntity {

    @Column(name = "tipo_pessoa", nullable = false)
    private String tipoPessoa;

    @Column(nullable = false)
    private String nome;

    @Column(name = "cpf_cnpj", unique = true)
    private String cpfCnpj;

    private String rg;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    private String sexo;

    @Column(name = "estado_civil")
    private String estadoCivil;

    private String profissao;
    private String email;
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
