package com.orionerp.modules.administration.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissoes")
@Getter
@Setter
public class Permissao extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String recurso;

    @Column(nullable = false, length = 50)
    private String acao;

    @Column(length = 300)
    private String descricao;

    @Column(nullable = false, length = 50)
    private String modulo;
}
