package com.orionerp.modules.pcp.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "estrutura_produto")
@Getter
@Setter
public class EstruturaProduto extends TenantEntity {

    @Column(name = "produto_pai_id", nullable = false)
    private Long produtoPaiId;

    @Column(name = "produto_componente_id", nullable = false)
    private Long produtoFilhoId;

    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal quantidade;

    @Column(name = "unidade_medida", length = 10)
    private String unidadeMedida;

    @Column(name = "perda_percentual", precision = 8, scale = 4)
    private BigDecimal perdaPercentual = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer sequencia = 1;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
