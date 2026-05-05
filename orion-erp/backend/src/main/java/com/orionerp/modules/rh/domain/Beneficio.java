package com.orionerp.modules.rh.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "beneficios")
@Getter
@Setter
public class Beneficio extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(name = "valor_empresa", precision = 18, scale = 2)
    private BigDecimal valorEmpresa;

    @Column(name = "valor_funcionario", precision = 18, scale = 2)
    private BigDecimal valorFuncionario;

    @Column(name = "desconto_folha")
    private Boolean descontoFolha = true;
}
