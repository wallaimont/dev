package com.orionerp.modules.patrimonio.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "depreciacao")
@Getter
@Setter
public class Depreciacao extends TenantEntity {

    @Column(name = "bem_patrimonial_id", nullable = false)
    private Long bemPatrimonialId;

    @Column(name = "data_calculo")
    private LocalDate dataDepreciacao = LocalDate.now();

    @Column(name = "valor_depreciacao", precision = 18, scale = 2)
    private BigDecimal valorDepreciacao = BigDecimal.ZERO;

    @Column(name = "valor_acumulado", precision = 18, scale = 2)
    private BigDecimal valorAcumulado = BigDecimal.ZERO;

    @Column(name = "valor_liquido", precision = 18, scale = 2)
    private BigDecimal valorLiquido = BigDecimal.ZERO;

    @Column(name = "mes")
    private Integer mesReferencia = LocalDate.now().getMonthValue();

    @Column(name = "ano")
    private Integer anoReferencia = LocalDate.now().getYear();

    @Column(name = "valor_base", precision = 18, scale = 2)
    private BigDecimal valorBase = BigDecimal.ZERO;

    @Column(name = "taxa_mensal", precision = 8, scale = 6)
    private BigDecimal taxaMensal = BigDecimal.ZERO;

    @Column(name = "valor_contabil_apos", precision = 18, scale = 2)
    private BigDecimal valorContabilApos = BigDecimal.ZERO;
}
