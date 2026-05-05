package com.orionerp.modules.faturamento.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NotaFiscalItemRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long notaFiscalId,
        Integer numeroItem,
        Long produtoId,
        String descricao,
        String ncm,
        String cfop,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        BigDecimal valorDesconto,
        String icmsCst,
        BigDecimal icmsBase,
        BigDecimal icmsAliquota,
        BigDecimal icmsValor,
        BigDecimal icmsStBase,
        BigDecimal icmsStAliquota,
        BigDecimal icmsStValor,
        String ipiCst,
        BigDecimal ipiBase,
        BigDecimal ipiAliquota,
        BigDecimal ipiValor,
        String pisCst,
        BigDecimal pisBase,
        BigDecimal pisAliquota,
        BigDecimal pisValor,
        String cofinsCst,
        BigDecimal cofinsBase,
        BigDecimal cofinsAliquota,
        BigDecimal cofinsValor
) {}
