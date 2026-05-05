package com.orionerp.modules.financeiro.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "conciliacao_bancaria_item")
@Getter
@Setter
public class ConciliacaoBancariaItem extends TenantEntity {

    @Column(name = "conciliacao_id", nullable = false)
    private Long conciliacaoId;

    @Column(name = "lancamento_financeiro_id")
    private Long lancamentoFinanceiroId;

    @Column(name = "data_extrato")
    private LocalDate dataExtrato;

    @Column(name = "descricao_extrato", columnDefinition = "TEXT")
    private String descricaoExtrato;

    @Column(name = "valor_extrato", precision = 18, scale = 2)
    private BigDecimal valorExtrato;

    @Column
    private Boolean conciliado = false;

    @Column(name = "data_conciliacao")
    private LocalDate dataConciliacao;
}
