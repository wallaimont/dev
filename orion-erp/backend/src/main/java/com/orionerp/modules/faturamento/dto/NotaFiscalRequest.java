package com.orionerp.modules.faturamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NotaFiscalRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotBlank String tipo,
        String serie,
        String numero,
        String chaveAcesso,
        String modelo,
        Long naturezaOperacaoId,
        String cfopPredominante,
        LocalDate dataEmissao,
        LocalDate dataSaidaEntrada,
        Long clienteId,
        Long fornecedorId,
        Long transportadoraId,
        String fretePorConta,
        BigDecimal valorProdutos,
        BigDecimal valorFrete,
        BigDecimal valorSeguro,
        BigDecimal valorDesconto,
        BigDecimal valorOutrasDespesas,
        BigDecimal valorIpi,
        BigDecimal valorIcms,
        BigDecimal valorIcmsSt,
        BigDecimal valorPis,
        BigDecimal valorCofins,
        BigDecimal valorTotal,
        String informacoesComplementares,
        Long pedidoVendaId,
        Long pedidoCompraId,
        String protocoloAutorizacao
) {}
