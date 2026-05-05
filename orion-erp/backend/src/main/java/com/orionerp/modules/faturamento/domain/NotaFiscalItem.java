package com.orionerp.modules.faturamento.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "nota_fiscal_itens")
@Getter
@Setter
public class NotaFiscalItem extends TenantEntity {

    @Column(name = "nota_fiscal_id", nullable = false)
    private Long notaFiscalId;

    @Column(name = "numero_item")
    private Integer numeroItem = 1;

    @Column(name = "produto_id")
    private Long produtoId;

    @Column(length = 200)
    private String descricao;

    @Column(length = 10)
    private String ncm;

    @Column(length = 10)
    private String cfop;

    @Column(name = "unidade_medida", length = 10)
    private String unidadeMedida = "UN";

    @Column(precision = 15, scale = 4)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", precision = 15, scale = 4)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_desconto", precision = 15, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    // ICMS
    @Column(name = "cst_icms", length = 5)
    private String icmsCst;

    @Column(name = "base_icms", precision = 15, scale = 2)
    private BigDecimal icmsBase = BigDecimal.ZERO;

    @Column(name = "aliquota_icms", precision = 5, scale = 2)
    private BigDecimal icmsAliquota = BigDecimal.ZERO;

    @Column(name = "valor_icms", precision = 15, scale = 2)
    private BigDecimal icmsValor = BigDecimal.ZERO;

    // ICMS ST
    @Column(name = "base_icms_st", precision = 15, scale = 2)
    private BigDecimal icmsStBase = BigDecimal.ZERO;

    @Column(name = "aliquota_icms_st", precision = 5, scale = 2)
    private BigDecimal icmsStAliquota = BigDecimal.ZERO;

    @Column(name = "valor_icms_st", precision = 15, scale = 2)
    private BigDecimal icmsStValor = BigDecimal.ZERO;

    // IPI
    @Column(name = "cst_ipi", length = 5)
    private String ipiCst;

    @Column(name = "base_ipi", precision = 15, scale = 2)
    private BigDecimal ipiBase = BigDecimal.ZERO;

    @Column(name = "aliquota_ipi", precision = 5, scale = 2)
    private BigDecimal ipiAliquota = BigDecimal.ZERO;

    @Column(name = "valor_ipi", precision = 15, scale = 2)
    private BigDecimal ipiValor = BigDecimal.ZERO;

    // PIS
    @Column(name = "cst_pis", length = 5)
    private String pisCst;

    @Column(name = "base_pis", precision = 15, scale = 2)
    private BigDecimal pisBase = BigDecimal.ZERO;

    @Column(name = "aliquota_pis", precision = 5, scale = 2)
    private BigDecimal pisAliquota = BigDecimal.ZERO;

    @Column(name = "valor_pis", precision = 15, scale = 2)
    private BigDecimal pisValor = BigDecimal.ZERO;

    // COFINS
    @Column(name = "cst_cofins", length = 5)
    private String cofinsCst;

    @Column(name = "base_cofins", precision = 15, scale = 2)
    private BigDecimal cofinsBase = BigDecimal.ZERO;

    @Column(name = "aliquota_cofins", precision = 5, scale = 2)
    private BigDecimal cofinsAliquota = BigDecimal.ZERO;

    @Column(name = "valor_cofins", precision = 15, scale = 2)
    private BigDecimal cofinsValor = BigDecimal.ZERO;
}
