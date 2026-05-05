package com.orionerp.modules.administration.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "empresas")
@Getter
@Setter
public class Empresa extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Column(nullable = false, length = 18)
    private String cnpj;

    @Column(length = 150)
    private String email;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
