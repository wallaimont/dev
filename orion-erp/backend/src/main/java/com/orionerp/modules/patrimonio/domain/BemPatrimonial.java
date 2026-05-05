package com.orionerp.modules.patrimonio.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bens_patrimoniais")
@Getter
@Setter
public class BemPatrimonial extends TenantEntity {

    @Column(length = 30, nullable = false)
    private String codigo;

    @Column(length = 200, nullable = false)
    private String descricao;

    @Column(name = "numero_patrimonio", length = 30)
    private String numeroPatrimonio;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;

    @Column(name = "valor_aquisicao", precision = 18, scale = 2)
    private BigDecimal valorAquisicao;

    @Column(name = "valor_residual", precision = 18, scale = 2)
    private BigDecimal valorResidual = BigDecimal.ZERO;

    @Column(name = "vida_util_meses")
    private Integer vidaUtilMeses = 60;

    @Column(name = "taxa_depreciacao_anual", precision = 8, scale = 4)
    private BigDecimal taxaDepreciacao = BigDecimal.ZERO;

    @Column(name = "grupo_bem", length = 50)
    private String grupo;

    @Column(length = 100)
    private String localizacao;

    @Column(name = "centro_custo_id")
    private Long centroCustoId;

    @Column(name = "fornecedor_id")
    private Long fornecedorId;

    @Column(name = "nota_fiscal_id")
    private Long notaFiscalId;

    @Column(name = "tipo_bem", length = 20)
    private String tipoBem = "TANGIVEL";

    @Column(name = "metodo_depreciacao", length = 20)
    private String metodoDepreciacao = "LINEAR";

    @Column(name = "valor_depreciado_acumulado", precision = 18, scale = 2)
    private BigDecimal valorDepreciadoAcumulado = BigDecimal.ZERO;

    @Column(name = "valor_contabil", precision = 18, scale = 2)
    private BigDecimal valorContabil = BigDecimal.ZERO;

    @Column(length = 20)
    private String status = "ATIVO";
}
