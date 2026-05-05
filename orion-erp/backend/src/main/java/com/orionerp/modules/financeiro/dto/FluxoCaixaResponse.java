package com.orionerp.modules.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FluxoCaixaResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long contaBancariaId,
        String tipo,
        BigDecimal valor,
        LocalDate dataLancamento,
        String descricao,
        Long tituloId,
        Long baixaId
) {
}
