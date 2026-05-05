package com.orionerp.modules.faturamento.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "notas_fiscais")
@Getter
@Setter
public class NotaFiscal extends TenantEntity {

    @Column(length = 10, nullable = false)
    private String tipo;

    @Column(length = 5)
    private String serie = "1";

    @Column(length = 20)
    private String numero;

    @Column(name = "chave_acesso", length = 50)
    private String chaveAcesso;

    @Column(length = 5)
    private String modelo = "55";

    @Column(name = "natureza_operacao_id")
    private Long naturezaOperacaoId;

    @Column(name = "cfop_predominante", length = 10)
    private String cfopPredominante;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao = LocalDate.now();

    @Column(name = "data_saida_entrada")
    private LocalDate dataSaidaEntrada;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "fornecedor_id")
    private Long fornecedorId;

    @Column(name = "transportadora_id")
    private Long transportadoraId;

    @Column(name = "frete_por_conta", length = 15)
    private String fretePorConta = "SEM_FRETE";

    @Column(name = "valor_produtos", precision = 15, scale = 2)
    private BigDecimal valorProdutos = BigDecimal.ZERO;

    @Column(name = "valor_frete", precision = 15, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Column(name = "valor_seguro", precision = 15, scale = 2)
    private BigDecimal valorSeguro = BigDecimal.ZERO;

    @Column(name = "valor_desconto", precision = 15, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_outras_despesas", precision = 15, scale = 2)
    private BigDecimal valorOutrasDespesas = BigDecimal.ZERO;

    @Column(name = "valor_ipi", precision = 15, scale = 2)
    private BigDecimal valorIpi = BigDecimal.ZERO;

    @Column(name = "valor_icms", precision = 15, scale = 2)
    private BigDecimal valorIcms = BigDecimal.ZERO;

    @Column(name = "valor_icms_st", precision = 15, scale = 2)
    private BigDecimal valorIcmsSt = BigDecimal.ZERO;

    @Column(name = "valor_pis", precision = 15, scale = 2)
    private BigDecimal valorPis = BigDecimal.ZERO;

    @Column(name = "valor_cofins", precision = 15, scale = 2)
    private BigDecimal valorCofins = BigDecimal.ZERO;

    @Column(name = "valor_total", precision = 15, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "informacoes_complementares", columnDefinition = "TEXT")
    private String informacoesComplementares;

    @Column(name = "pedido_venda_id")
    private Long pedidoVendaId;

    @Column(name = "pedido_compra_id")
    private Long pedidoCompraId;

    @Column(length = 20, nullable = false)
    private String status = "DIGITADA";

    @Column(name = "protocolo_autorizacao", length = 20)
    private String protocoloAutorizacao;
}
