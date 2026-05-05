package com.orionerp.modules.seguros.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seguradoras")
@Getter
@Setter
public class Seguradora extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 18)
    private String cnpj;

    @Column(name = "registro_susep", length = 30)
    private String registroSusep;

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

    @Column(length = 100)
    private String contato;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
