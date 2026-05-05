package com.orionerp.modules.financeiro.domain;

import com.orionerp.common.TenantEntity;
import com.orionerp.modules.cadastros.domain.CentroCusto;
import com.orionerp.modules.cadastros.domain.Cliente;
import com.orionerp.modules.cadastros.domain.ContaBancaria;
import com.orionerp.modules.cadastros.domain.Fornecedor;
import com.orionerp.modules.cadastros.domain.NaturezaFinanceira;
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
@Table(name = "titulos")
@Getter
@Setter
public class Titulo extends TenantEntity {

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, length = 30)
    private String numero;

    @Column(length = 10)
    private String serie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "natureza_financeira_id")
    private NaturezaFinanceira naturezaFinanceira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_bancaria_id")
    private ContaBancaria contaBancaria;

    @Column(name = "documento_origem", length = 50)
    private String documentoOrigem;

    @Column(name = "documento_origem_id")
    private Long documentoOrigemId;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "valor_original", nullable = false)
    private BigDecimal valorOriginal;

    @Column(name = "valor_aberto", nullable = false)
    private BigDecimal valorAberto;

    @Column(nullable = false, length = 20)
    private String status = "ABERTO";

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "titulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroParcela ASC")
    private List<TituloParcela> parcelas = new ArrayList<>();

    public void addParcela(TituloParcela parcela) {
        parcela.setTitulo(this);
        parcelas.add(parcela);
    }

    public void recalcularAberto() {
        this.valorAberto = parcelas.stream()
                .filter(p -> !"CANCELADO".equals(p.getStatus()))
                .map(p -> p.getValor().subtract(p.getValorPago()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (valorAberto.compareTo(BigDecimal.ZERO) <= 0) {
            this.status = "QUITADO";
            this.valorAberto = BigDecimal.ZERO;
        } else if (valorAberto.compareTo(valorOriginal) < 0) {
            this.status = "PARCIAL";
        } else {
            this.status = "ABERTO";
        }
    }
}
