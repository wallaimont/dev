package com.orionerp.modules.vendas.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tabela_preco_itens")
@Getter
@Setter
public class TabelaPrecoItem extends TenantEntity {

    @Column(name = "tabela_preco_id", nullable = false)
    private Long tabelaPrecoId;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(precision = 18, scale = 2)
    private BigDecimal preco;

    @Column(name = "preco_promocional", precision = 18, scale = 2)
    private BigDecimal precoPromocional;

    @Column(name = "quantidade_minima", precision = 18, scale = 6)
    private BigDecimal quantidadeMinima;
}
