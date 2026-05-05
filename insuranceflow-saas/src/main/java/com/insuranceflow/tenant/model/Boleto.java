package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "boleto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Boleto extends TenantBaseEntity {
    @Column(name = "lancamento_id") private UUID lancamentoId;
    @Column(name = "apolice_id") private UUID apoliceId;
    @Column(name = "cliente_id") private UUID clienteId;
    @Column(name = "numero_parcela") private Integer numeroParcela;
    @Column(nullable = false) private BigDecimal valor;
    @Column(name = "data_vencimento", nullable = false) private LocalDate dataVencimento;
    @Column(name = "data_pagamento") private LocalDate dataPagamento;
    @Column(nullable = false) private String status;
    @Column(name = "linha_digitavel") private String linhaDigitavel;
    @Column(name = "codigo_barras") private String codigoBarras;
    @Column(name = "url_boleto") private String urlBoleto;
    @Column(name = "nosso_numero") private String nossoNumero;
    private String observacoes;
}
