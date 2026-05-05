package com.orionerp.modules.estoque.domain;

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
import java.util.UUID;

@Entity
@Table(name = "movimentacoes_estoque")
@Getter
@Setter
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "filial_id", nullable = false)
    private Long filialId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "armazem_id", nullable = false)
    private Armazem armazem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacao_id")
    private Localizacao localizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false, length = 15)
    private String tipo;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Column(name = "custo_unitario", nullable = false)
    private BigDecimal custoUnitario = BigDecimal.ZERO;

    @Column(name = "custo_total", nullable = false)
    private BigDecimal custoTotal = BigDecimal.ZERO;

    @Column(name = "saldo_anterior", nullable = false)
    private BigDecimal saldoAnterior = BigDecimal.ZERO;

    @Column(name = "saldo_posterior", nullable = false)
    private BigDecimal saldoPosterior = BigDecimal.ZERO;

    @Column(length = 50)
    private String lote;

    private LocalDate validade;

    @Column(name = "documento_tipo", length = 30)
    private String documentoTipo;

    @Column(name = "documento_id")
    private Long documentoId;

    @Column(name = "documento_numero", length = 30)
    private String documentoNumero;

    @Column(length = 300)
    private String observacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(updatable = false, length = 100)
    private String createdBy;
}
