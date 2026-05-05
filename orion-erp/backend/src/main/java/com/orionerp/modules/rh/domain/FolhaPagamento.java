package com.orionerp.modules.rh.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "folha_pagamento")
@Getter
@Setter
public class FolhaPagamento extends TenantEntity {

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false, length = 30)
    private String tipo = "MENSAL";

    @Column(name = "data_calculo")
    private LocalDate dataCalculo;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "total_proventos", precision = 18, scale = 2)
    private BigDecimal totalProventos = BigDecimal.ZERO;

    @Column(name = "total_descontos", precision = 18, scale = 2)
    private BigDecimal totalDescontos = BigDecimal.ZERO;

    @Column(name = "total_liquido", precision = 18, scale = 2)
    private BigDecimal totalLiquido = BigDecimal.ZERO;

    @Column(name = "total_encargos", precision = 18, scale = 2)
    private BigDecimal totalEncargos = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "ABERTA";
}
