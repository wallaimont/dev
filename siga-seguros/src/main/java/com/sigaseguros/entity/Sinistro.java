package com.sigaseguros.entity;

import com.sigaseguros.enums.StatusSinistro;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sinistros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sinistro extends BaseEntity {

    @Column(name = "numero_sinistro", nullable = false, unique = true, length = 30)
    private String numeroSinistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apolice_id", nullable = false)
    private Apolice apolice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguradora_id", nullable = false)
    private Seguradora seguradora;

    @Column(name = "data_aviso", nullable = false)
    private LocalDate dataAviso;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "valor_estimado", precision = 15, scale = 2)
    private BigDecimal valorEstimado;

    @Column(name = "valor_pago", precision = 15, scale = 2)
    private BigDecimal valorPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusSinistro status;

    @Column(name = "responsavel_interno", length = 150)
    private String responsavelInterno;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "sinistro", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Documento> documentos = new ArrayList<>();
}
