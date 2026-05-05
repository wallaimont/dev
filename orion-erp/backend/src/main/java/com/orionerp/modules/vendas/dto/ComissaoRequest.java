package com.orionerp.modules.vendas.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ComissaoRequest(
    @NotNull Long empresaId,
    Long filialId,
    @NotNull Long vendedorId,
    Long pedidoVendaId,
    @NotNull BigDecimal percentual,
    @NotNull BigDecimal valorBase,
    @NotNull BigDecimal valorComissao,
    String status
) {}
