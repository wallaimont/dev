package com.orionerp.modules.estoque.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "armazens")
@Getter
@Setter
public class Armazem extends TenantEntity {

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 20)
    private String tipo = "PRINCIPAL";
}
