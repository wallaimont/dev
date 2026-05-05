package com.orionerp.modules.estoque.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotNull Long armazemId,
        Long localizacaoId,
        @NotNull Long produtoId,
        @NotNull @Size(max = 15) String tipo,
        @NotNull @Positive BigDecimal quantidade,
        BigDecimal custoUnitario,
        @Size(max = 50) String lote,
        LocalDate validade,
        @Size(max = 30) String documentoTipo,
        Long documentoId,
        @Size(max = 30) String documentoNumero,
        @Size(max = 300) String observacao
) {
}
