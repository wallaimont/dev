package com.sigaseguros.entity;

import com.sigaseguros.enums.StatusProposta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "propostas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposta extends BaseEntity {

    @Column(name = "numero_proposta", nullable = false, unique = true, length = 30)
    private String numeroProposta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguradora_id", nullable = false)
    private Seguradora seguradora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corretora_id")
    private Corretora corretora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ramo_seguro_id", nullable = false)
    private RamoSeguro ramoSeguro;

    @Column(name = "vigencia_inicial")
    private LocalDate vigenciaInicial;

    @Column(name = "vigencia_final")
    private LocalDate vigenciaFinal;

    @Column(name = "premio_liquido", precision = 15, scale = 2)
    private BigDecimal premioLiquido;

    @Column(name = "premio_total", precision = 15, scale = 2)
    private BigDecimal premioTotal;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @Column(name = "valor_comissao", precision = 15, scale = 2)
    private BigDecimal valorComissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusProposta status;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "responsavel_interno", length = 150)
    private String responsavelInterno;

    @OneToMany(mappedBy = "proposta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Documento> documentos = new ArrayList<>();
}
