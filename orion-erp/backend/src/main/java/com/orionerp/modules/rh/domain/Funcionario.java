package com.orionerp.modules.rh.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
public class Funcionario extends TenantEntity {

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(length = 20)
    private String rg;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 1)
    private String sexo;

    @Column(name = "estado_civil", length = 20)
    private String estadoCivil;

    @Column(length = 300)
    private String endereco;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 10)
    private String cep;

    @Column(length = 20)
    private String telefone;

    @Column(length = 150)
    private String email;

    @Column(name = "departamento_id")
    private Long departamentoId;

    @Column(name = "cargo_id")
    private Long cargoId;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(name = "data_demissao")
    private LocalDate dataDemissao;

    @Column(precision = 18, scale = 2)
    private BigDecimal salario;

    @Column(nullable = false, length = 20)
    private String situacao = "ATIVO";

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(length = 20)
    private String pis;

    @Column(length = 20)
    private String ctps;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
