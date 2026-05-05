package com.orionerp.modules.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TituloRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotBlank @Size(max = 10) String tipo,
        @NotBlank @Size(max = 30) String numero,
        @Size(max = 10) String serie,
        Long clienteId,
        Long fornecedorId,
        Long naturezaFinanceiraId,
        Long centroCustoId,
        Long contaBancariaId,
        @Size(max = 50) String documentoOrigem,
        Long documentoOrigemId,
        @NotNull LocalDate dataEmissao,
        @NotNull @Positive BigDecimal valorOriginal,
        String observacao,
        List<ParcelaInput> parcelas
) {
    public record ParcelaInput(
            @NotNull Integer numeroParcela,
            @NotNull LocalDate dataVencimento,
            @NotNull @Positive BigDecimal valor
    ) {
    }
}
