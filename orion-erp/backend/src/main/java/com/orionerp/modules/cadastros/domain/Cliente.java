package com.orionerp.modules.cadastros.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class Cliente extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(name = "tipo_pessoa", nullable = false, length = 1)
    private String tipoPessoa = "J";

    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Column(name = "cpf_cnpj", nullable = false, length = 18)
    private String cpfCnpj;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Column(length = 300)
    private String endereco;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 10)
    private String cep;

    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String celular;

    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String website;

    @Column(name = "limite_credito", nullable = false)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Column(name = "saldo_devedor", nullable = false)
    private BigDecimal saldoDevedor = BigDecimal.ZERO;

    @Column(name = "bloqueio_financeiro", nullable = false)
    private Boolean bloqueioFinanceiro = false;

    @Column(name = "bloqueio_comercial", nullable = false)
    private Boolean bloqueioComercial = false;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
