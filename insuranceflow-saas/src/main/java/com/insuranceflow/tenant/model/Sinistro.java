package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sinistro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sinistro extends TenantBaseEntity {

    @Column(name = "numero_sinistro") private String numeroSinistro;
    @Column(name = "apolice_id", nullable = false) private UUID apoliceId;
    @Column(name = "cliente_id", nullable = false) private UUID clienteId;
    @Column(name = "data_ocorrencia", nullable = false) private LocalDate dataOcorrencia;
    @Column(name = "data_aviso") private LocalDate dataAviso;
    private String tipo;
    @Column(nullable = false) private String descricao;
    @Column(nullable = false) private String status;
    @Column(name = "valor_estimado") private BigDecimal valorEstimado;
    @Column(name = "valor_indenizado") private BigDecimal valorIndenizado;
    @Column(name = "data_pagamento_indenizacao") private LocalDate dataPagamentoIndenizacao;
    @Column(name = "local_ocorrencia") private String localOcorrencia;
    @Column(name = "boletim_ocorrencia") private String boletimOcorrencia;
    private String observacoes;
}
