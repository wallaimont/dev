package com.orionerp.modules.compras.domain;

import com.orionerp.modules.cadastros.domain.Produto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recebimento_itens")
@Getter
@Setter
public class RecebimentoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebimento_id", nullable = false)
    private Recebimento recebimento;

    @Column(name = "pedido_compra_item_id")
    private Long pedidoCompraItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column(length = 50)
    private String lote;

    @Column
    private LocalDate validade;

    @Column(name = "armazem_id")
    private Long armazemId;

    @Column(name = "localizacao_id")
    private Long localizacaoId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
