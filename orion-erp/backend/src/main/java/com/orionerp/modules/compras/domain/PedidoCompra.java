package com.orionerp.modules.compras.domain;

import com.orionerp.common.TenantEntity;
import com.orionerp.modules.cadastros.domain.CondicaoPagamento;
import com.orionerp.modules.cadastros.domain.Fornecedor;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos_compra")
@Getter
@Setter
public class PedidoCompra extends TenantEntity {

    @Column(nullable = false, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(name = "cotacao_id")
    private Long cotacaoId;

    @Column(name = "data_pedido", nullable = false)
    private LocalDate dataPedido = LocalDate.now();

    @Column(name = "data_previsao_entrega")
    private LocalDate dataPrevisaoEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicao_pagamento_id")
    private CondicaoPagamento condicaoPagamento;

    @Column(name = "comprador_id")
    private Long compradorId;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "valor_frete", nullable = false)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Column(name = "valor_desconto", nullable = false)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "pedidoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PedidoCompraItem> itens = new ArrayList<>();

    public void addItem(PedidoCompraItem item) {
        item.setPedidoCompra(this);
        itens.add(item);
    }

    public void recalcularTotal() {
        BigDecimal totalItens = itens.stream()
                .map(PedidoCompraItem::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorTotal = totalItens.add(valorFrete).subtract(valorDesconto);
    }
}
