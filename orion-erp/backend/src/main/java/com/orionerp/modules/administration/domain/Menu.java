package com.orionerp.modules.administration.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "menus")
@Getter
@Setter
public class Menu extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 50)
    private String icone;

    @Column(length = 200)
    private String rota;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(length = 50)
    private String modulo;

    @Column(name = "permissao_recurso", length = 100)
    private String permissaoRecurso;
}
