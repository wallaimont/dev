package com.orionerp.modules.rh.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ferias")
@Getter
@Setter
public class Ferias extends TenantEntity {

    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;

    @Column(name = "periodo_aquisitivo_inicio", nullable = false)
    private LocalDate periodoAquisitivoInicio;

    @Column(name = "periodo_aquisitivo_fim", nullable = false)
    private LocalDate periodoAquisitivoFim;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "dias_gozo", nullable = false)
    private Integer diasGozo;

    @Column(name = "dias_abono")
    private Integer diasAbono = 0;

    @Column(name = "valor_ferias", precision = 18, scale = 2)
    private BigDecimal valorFerias = BigDecimal.ZERO;

    @Column(name = "valor_abono", precision = 18, scale = 2)
    private BigDecimal valorAbono = BigDecimal.ZERO;

    @Column(name = "valor_adiantamento_13", precision = 18, scale = 2)
    private BigDecimal valorAdiantamento13 = BigDecimal.ZERO;

    @Column(name = "valor_terco", precision = 18, scale = 2)
    private BigDecimal valorTerco = BigDecimal.ZERO;

    @Column(name = "total_bruto", precision = 18, scale = 2)
    private BigDecimal totalBruto = BigDecimal.ZERO;

    @Column(name = "total_descontos", precision = 18, scale = 2)
    private BigDecimal totalDescontosFer = BigDecimal.ZERO;

    @Column(name = "total_liquido", precision = 18, scale = 2)
    private BigDecimal totalLiquidoFer = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "PROGRAMADA";
}
