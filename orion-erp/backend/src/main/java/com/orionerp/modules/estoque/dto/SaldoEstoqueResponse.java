package com.orionerp.modules.estoque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaldoEstoqueResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long armazemId,
        String armazemNome,
        Long localizacaoId,
        Long produtoId,
        String produtoCodigo,
        String produtoNome,
        String lote,
        LocalDate validade,
        BigDecimal quantidade,
        BigDecimal custoMedio,
        BigDecimal reservado,
        BigDecimal disponivel
) {
}
