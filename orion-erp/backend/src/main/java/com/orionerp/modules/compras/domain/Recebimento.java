package com.orionerp.modules.compras.domain;

import com.orionerp.common.TenantEntity;
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
@Table(name = "recebimentos")
@Getter
@Setter
public class Recebimento extends TenantEntity {

    @Column(nullable = false, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_compra_id", nullable = false)
    private PedidoCompra pedidoCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(name = "data_recebimento", nullable = false)
    private LocalDate dataRecebimento = LocalDate.now();

    @Column(name = "numero_nf", length = 30)
    private String numeroNf;

    @Column(name = "serie_nf", length = 10)
    private String serieNf;

    @Column(name = "chave_nfe", length = 50)
    private String chaveNfe;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "recebimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<RecebimentoItem> itens = new ArrayList<>();

    public void addItem(RecebimentoItem item) {
        item.setRecebimento(this);
        itens.add(item);
    }

    public void recalcularTotal() {
        this.valorTotal = itens.stream()
                .map(RecebimentoItem::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
