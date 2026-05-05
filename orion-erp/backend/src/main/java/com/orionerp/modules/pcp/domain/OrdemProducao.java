package com.orionerp.modules.pcp.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ordens_producao")
@Getter
@Setter
public class OrdemProducao extends TenantEntity {

    @Column(length = 30, nullable = false)
    private String numero;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "quantidade_prevista", precision = 18, scale = 6, nullable = false)
    private BigDecimal quantidade;

    @Column(name = "data_abertura")
    private LocalDate dataAbertura = LocalDate.now();

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_previsao")
    private LocalDate dataPrevisaoFim = LocalDate.now();

    @Column(name = "data_encerramento")
    private LocalDate dataFim;

    @Column(length = 20)
    private String status = "PLANEJADA";

    @Column(length = 20)
    private String prioridade = "NORMAL";

    @Column(name = "centro_custo_id")
    private Long centroCustoId;

    @Column(name = "quantidade_produzida", precision = 18, scale = 6)
    private BigDecimal quantidadeProduzida = BigDecimal.ZERO;

    @Column(name = "quantidade_perda", precision = 18, scale = 6)
    private BigDecimal quantidadePerda = BigDecimal.ZERO;

    @Column(name = "custo_previsto", precision = 18, scale = 2)
    private BigDecimal custoPrevisto = BigDecimal.ZERO;

    @Column(name = "custo_real", precision = 18, scale = 2)
    private BigDecimal custoReal = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
