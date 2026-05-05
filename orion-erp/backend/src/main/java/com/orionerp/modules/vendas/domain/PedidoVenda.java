package com.orionerp.modules.vendas.domain;

import com.orionerp.common.TenantEntity;
import com.orionerp.modules.cadastros.domain.Cliente;
import com.orionerp.modules.cadastros.domain.CondicaoPagamento;
import com.orionerp.modules.cadastros.domain.TabelaPreco;
import com.orionerp.modules.cadastros.domain.Transportadora;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos_venda")
@Getter
@Setter
public class PedidoVenda extends TenantEntity {

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(name = "orcamento_id")
    private Long orcamentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "vendedor_id")
    private Long vendedorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportadora_id")
    private Transportadora transportadora;

    @Column(name = "data_pedido", nullable = false)
    private LocalDate dataPedido = LocalDate.now();

    @Column(name = "data_previsao_entrega")
    private LocalDate dataPrevisaoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicao_pagamento_id")
    private CondicaoPagamento condicaoPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabela_preco_id")
    private TabelaPreco tabelaPreco;

    @Column(name = "valor_produtos", nullable = false)
    private BigDecimal valorProdutos = BigDecimal.ZERO;

    @Column(name = "valor_desconto", nullable = false)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_frete", nullable = false)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(name = "aprovado_por")
    private Long aprovadoPor;

    @Column(name = "aprovado_em")
    private LocalDateTime aprovadoEm;

    @Column(name = "bloqueio_motivo", length = 300)
    private String bloqueioMotivo;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "pedidoVenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PedidoVendaItem> itens = new ArrayList<>();

    public void addItem(PedidoVendaItem item) {
        item.setPedidoVenda(this);
        itens.add(item);
    }

    public void recalcularTotais() {
        this.valorProdutos = itens.stream()
                .map(PedidoVendaItem::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorTotal = valorProdutos.add(valorFrete).subtract(valorDesconto);
    }
}
