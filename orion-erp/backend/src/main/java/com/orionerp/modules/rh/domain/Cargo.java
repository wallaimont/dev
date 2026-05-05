package com.orionerp.modules.rh.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cargos")
@Getter
@Setter
public class Cargo extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 10)
    private String cbo;

    @Column(name = "salario_base", precision = 18, scale = 2)
    private BigDecimal salarioBase;

    @Column(length = 50)
    private String nivel;
}
