package com.orionerp.modules.fiscal.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "naturezas_operacao")
@Getter
@Setter
public class NaturezaOperacao extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 10)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cfop_id")
    private Cfop cfop;

    @Column(name = "gera_financeiro", nullable = false)
    private Boolean geraFinanceiro = true;

    @Column(name = "movimenta_estoque", nullable = false)
    private Boolean movimentaEstoque = true;
}
