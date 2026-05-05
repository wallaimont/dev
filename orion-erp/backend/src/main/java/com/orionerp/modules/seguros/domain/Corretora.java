package com.orionerp.modules.seguros.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "corretoras")
@Getter
@Setter
public class Corretora extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 18)
    private String cnpj;

    @Column(length = 100)
    private String responsavel;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 300)
    private String endereco;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 10)
    private String cep;

    @Column(name = "percentual_comissao", nullable = false)
    private BigDecimal percentualComissao = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
