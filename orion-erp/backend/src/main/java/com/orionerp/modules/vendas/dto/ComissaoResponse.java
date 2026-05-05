package com.orionerp.modules.vendas.dto;

import com.orionerp.modules.vendas.domain.Comissao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ComissaoResponse(
    Long id,
    UUID uuid,
    Long empresaId,
    Long filialId,
    Long vendedorId,
    Long pedidoVendaId,
    BigDecimal percentual,
    BigDecimal valorBase,
    BigDecimal valorComissao,
    String status,
    LocalDate dataPagamento,
    LocalDateTime createdAt
) {
    public static ComissaoResponse from(Comissao e) {
        return new ComissaoResponse(
            e.getId(), e.getUuid(), e.getEmpresaId(), e.getFilialId(),
            e.getVendedorId(), e.getPedidoVendaId(),
            e.getPercentual(), e.getValorBase(), e.getValorComissao(),
            e.getStatus(), e.getDataPagamento(), e.getCreatedAt());
    }
}
