package com.orionerp.modules.contabilidade.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamento_contabil")
@Getter
@Setter
public class LancamentoContabil extends TenantEntity {

    @Column(nullable = false, length = 20)
    private String lote;

    @Column(length = 20)
    private String sublote;

    @Column(nullable = false)
    private Integer numero;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "conta_debito_id", nullable = false)
    private Long contaDebitoId;

    @Column(name = "conta_credito_id", nullable = false)
    private Long contaCreditoId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 500)
    private String historico;

    @Column(length = 50)
    private String documento;

    @Column(name = "centro_custo_id")
    private Long centroCustoId;

    @Column(name = "centro_resultado_id")
    private Long centroResultadoId;

    @Column(nullable = false, length = 20)
    private String tipo = "NORMAL"; // NORMAL, PARTIDA_DOBRADA, ESTORNO, ENCERRAMENTO, ABERTURA

    @Column(length = 30)
    private String origem; // MANUAL, FINANCEIRO, FATURAMENTO, FOLHA, PATRIMONIO

    @Column(name = "origem_id")
    private Long origemId;

    @Column(nullable = false, length = 20)
    private String status = "DIGITADO"; // DIGITADO, VERIFICADO, APROVADO, ESTORNADO
}
