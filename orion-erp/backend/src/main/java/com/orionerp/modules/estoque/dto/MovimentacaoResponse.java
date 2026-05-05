package com.orionerp.modules.estoque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long armazemId,
        String armazemNome,
        Long localizacaoId,
        Long produtoId,
        String produtoCodigo,
        String produtoNome,
        String tipo,
        BigDecimal quantidade,
        BigDecimal custoUnitario,
        BigDecimal custoTotal,
        BigDecimal saldoAnterior,
        BigDecimal saldoPosterior,
        String lote,
        LocalDate validade,
        String documentoTipo,
        Long documentoId,
        String documentoNumero,
        String observacao,
        LocalDateTime createdAt
) {
}
