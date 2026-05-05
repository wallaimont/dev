package com.orionerp.modules.cadastros.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "contas_bancarias")
@Getter
@Setter
public class ContaBancaria extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "filial_id")
    private Long filialId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "banco_id", nullable = false)
    private Banco banco;

    @Column(nullable = false, length = 20)
    private String agencia;

    @Column(nullable = false, length = 30)
    private String conta;

    @Column(length = 5)
    private String digito;

    @Column(nullable = false, length = 20)
    private String tipo = "CORRENTE";

    @Column(length = 200)
    private String descricao;

    @Column(name = "saldo_inicial", nullable = false)
    private BigDecimal saldoInicial = BigDecimal.ZERO;
}
