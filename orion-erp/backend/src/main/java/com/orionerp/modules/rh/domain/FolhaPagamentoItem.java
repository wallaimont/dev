package com.orionerp.modules.rh.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "folha_pagamento_item")
@Getter
@Setter
public class FolhaPagamentoItem extends TenantEntity {

    @Column(name = "folha_pagamento_id", nullable = false)
    private Long folhaPagamentoId;

    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;

    @Column(name = "salario_base", precision = 18, scale = 2)
    private BigDecimal salarioBase = BigDecimal.ZERO;

    @Column(name = "horas_extras_valor", precision = 18, scale = 2)
    private BigDecimal horasExtrasValor;

    @Column(name = "adicional_noturno", precision = 18, scale = 2)
    private BigDecimal adicionalNoturno = BigDecimal.ZERO;

    @Column(name = "adicional_periculosidade", precision = 18, scale = 2)
    private BigDecimal adicionalPericulosidade = BigDecimal.ZERO;

    @Column(name = "adicional_insalubridade", precision = 18, scale = 2)
    private BigDecimal adicionalInsalubridade = BigDecimal.ZERO;

    @Column(name = "comissoes", precision = 18, scale = 2)
    private BigDecimal comissoes;

    @Column(name = "gratificacoes", precision = 18, scale = 2)
    private BigDecimal gratificacoes;

    @Column(name = "outros_proventos", precision = 18, scale = 2)
    private BigDecimal outrosProventos = BigDecimal.ZERO;

    @Column(name = "desconto_inss", precision = 18, scale = 2)
    private BigDecimal descontoInss;

    @Column(name = "desconto_irrf", precision = 18, scale = 2)
    private BigDecimal descontoIrrf;

    @Column(name = "desconto_vt", precision = 18, scale = 2)
    private BigDecimal descontoVt;

    @Column(name = "desconto_vr", precision = 18, scale = 2)
    private BigDecimal descontoVr;

    @Column(name = "desconto_plano_saude", precision = 18, scale = 2)
    private BigDecimal descontoPlanoSaude;

    @Column(name = "desconto_sindical", precision = 18, scale = 2)
    private BigDecimal descontoSindical;

    @Column(name = "outros_descontos", precision = 18, scale = 2)
    private BigDecimal outrosDescontos = BigDecimal.ZERO;

    @Column(name = "salario_liquido", precision = 18, scale = 2)
    private BigDecimal salarioLiquido = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal fgts = BigDecimal.ZERO;

    @Column(name = "inss_empresa", precision = 18, scale = 2)
    private BigDecimal inssEmpresa = BigDecimal.ZERO;
}
