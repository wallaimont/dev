package com.sigaseguros.entity;

import com.sigaseguros.enums.StatusComissao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "comissoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comissao extends BaseEntity {

    @Column(length = 100)
    private String origem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposta_id")
    private Proposta proposta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apolice_id")
    private Apolice apolice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguradora_id")
    private Seguradora seguradora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corretora_id")
    private Corretora corretora;

    @Column(name = "favorecido", length = 200)
    private String favorecido;

    @Column(name = "percentual", precision = 5, scale = 2)
    private BigDecimal percentual;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusComissao status;

    @Column(name = "data_prevista")
    private LocalDate dataPrevista;

    @Column(name = "data_recebimento")
    private LocalDate dataRecebimento;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
