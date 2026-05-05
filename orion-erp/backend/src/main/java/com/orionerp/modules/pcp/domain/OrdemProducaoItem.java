package com.orionerp.modules.pcp.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ordem_producao_itens")
@Getter
@Setter
public class OrdemProducaoItem extends TenantEntity {

    @Column(name = "ordem_producao_id", nullable = false)
    private Long ordemProducaoId;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "quantidade_prevista", precision = 18, scale = 6)
    private BigDecimal quantidadePrevista = BigDecimal.ZERO;

    @Column(name = "quantidade_consumida", precision = 18, scale = 6)
    private BigDecimal quantidadeUtilizada = BigDecimal.ZERO;

    @Column(name = "unidade_medida", length = 10)
    private String unidadeMedida;

    @Column(name = "custo_unitario", precision = 18, scale = 2)
    private BigDecimal custoUnitario = BigDecimal.ZERO;
}
