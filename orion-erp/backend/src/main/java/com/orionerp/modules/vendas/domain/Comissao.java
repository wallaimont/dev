package com.orionerp.modules.vendas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comissoes")
@Getter
@Setter
public class Comissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "filial_id")
    private Long filialId;

    @Column(name = "vendedor_id", nullable = false)
    private Long vendedorId;

    @Column(name = "pedido_venda_id")
    private Long pedidoVendaId;

    @Column(nullable = false)
    private BigDecimal percentual;

    @Column(name = "valor_base", nullable = false)
    private BigDecimal valorBase;

    @Column(name = "valor_comissao", nullable = false)
    private BigDecimal valorComissao;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
