package com.orionerp.modules.financeiro.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "conciliacao_bancaria")
@Getter
@Setter
public class ConciliacaoBancaria extends TenantEntity {

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "saldo_banco", precision = 18, scale = 2)
    private BigDecimal saldoExtrato = BigDecimal.ZERO;

    @Column(name = "saldo_sistema", precision = 18, scale = 2)
    private BigDecimal saldoSistema = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal diferenca = BigDecimal.ZERO;

    @Column(length = 20)
    private String status = "EM_ANDAMENTO";

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
