package com.orionerp.modules.patrimonio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BemPatrimonialRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotBlank String codigo,
        @NotBlank String descricao,
        String numeroPatrimonio,
        LocalDate dataAquisicao,
        BigDecimal valorAquisicao,
        BigDecimal valorResidual,
        Integer vidaUtilMeses,
        BigDecimal taxaDepreciacao,
        String grupo,
        String localizacao,
        Long centroCustoId,
        Long fornecedorId,
        Long notaFiscalId,
        String status
) {}
