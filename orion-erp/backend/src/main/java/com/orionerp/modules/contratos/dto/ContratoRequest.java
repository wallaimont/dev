package com.orionerp.modules.contratos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotBlank String numero,
        @NotNull Long clienteId,
        String tipo,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim,
        LocalDate dataAssinatura,
        BigDecimal valorTotal,
        BigDecimal valorMensal,
        String formaPagamento,
        Integer diaVencimento,
        String status,
        String observacao
) {}
