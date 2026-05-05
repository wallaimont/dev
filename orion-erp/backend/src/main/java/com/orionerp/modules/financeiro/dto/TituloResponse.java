package com.orionerp.modules.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TituloResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        String tipo,
        String numero,
        String serie,
        Long clienteId,
        String clienteNome,
        Long fornecedorId,
        String fornecedorNome,
        Long naturezaFinanceiraId,
        String naturezaNome,
        Long centroCustoId,
        String centroCustoNome,
        Long contaBancariaId,
        String documentoOrigem,
        LocalDate dataEmissao,
        BigDecimal valorOriginal,
        BigDecimal valorAberto,
        String status,
        String observacao,
        List<ParcelaResponse> parcelas
) {
    public record ParcelaResponse(
            Long id,
            UUID uuid,
            Integer numeroParcela,
            LocalDate dataVencimento,
            BigDecimal valor,
            BigDecimal valorPago,
            BigDecimal valorJuros,
            BigDecimal valorMulta,
            BigDecimal valorDesconto,
            String status,
            LocalDate dataPagamento
    ) {
    }
}
