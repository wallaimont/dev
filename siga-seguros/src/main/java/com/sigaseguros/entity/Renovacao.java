package com.sigaseguros.entity;

import com.sigaseguros.enums.StatusRenovacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "renovacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Renovacao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apolice_id", nullable = false)
    private Apolice apolice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusRenovacao status;

    @Column(name = "responsavel", length = 150)
    private String responsavel;

    @Column(name = "data_ultimo_contato")
    private LocalDate dataUltimoContato;

    @Column(name = "retorno_cliente", columnDefinition = "TEXT")
    private String retornoCliente;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nova_proposta_id")
    private Proposta novaProposta;
}
