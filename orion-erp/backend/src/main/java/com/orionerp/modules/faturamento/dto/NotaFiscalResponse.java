package com.orionerp.modules.faturamento.dto;

import com.orionerp.modules.faturamento.domain.NotaFiscal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotaFiscalResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        String tipo,
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
        String status,
        String protocoloAutorizacao,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NotaFiscalResponse from(NotaFiscal e) {
        return new NotaFiscalResponse(
                e.getId(), e.getUuid(), e.getEmpresaId(), e.getFilialId(),
                e.getTipo(), e.getSerie(), e.getNumero(), e.getChaveAcesso(),
                e.getModelo(), e.getNaturezaOperacaoId(), e.getCfopPredominante(),
                e.getDataEmissao(), e.getDataSaidaEntrada(),
                e.getClienteId(), e.getFornecedorId(), e.getTransportadoraId(),
                e.getFretePorConta(), e.getValorProdutos(), e.getValorFrete(),
                e.getValorSeguro(), e.getValorDesconto(), e.getValorOutrasDespesas(),
                e.getValorIpi(), e.getValorIcms(), e.getValorIcmsSt(),
                e.getValorPis(), e.getValorCofins(), e.getValorTotal(),
                e.getInformacoesComplementares(), e.getPedidoVendaId(), e.getPedidoCompraId(),
                e.getStatus(), e.getProtocoloAutorizacao(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
