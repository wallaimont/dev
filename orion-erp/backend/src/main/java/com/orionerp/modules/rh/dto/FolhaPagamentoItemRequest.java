package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FolhaPagamentoItemRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long folhaPagamentoId,
        @NotNull Long funcionarioId,
        BigDecimal salarioBase,
        BigDecimal horasExtrasValor,
        BigDecimal adicionalNoturno,
        BigDecimal adicionalPericulosidade,
        BigDecimal adicionalInsalubridade,
        BigDecimal comissoes,
        BigDecimal gratificacoes,
        BigDecimal outrosProventos,
        BigDecimal descontoInss,
        BigDecimal descontoIrrf,
        BigDecimal descontoVt,
        BigDecimal descontoVr,
        BigDecimal descontoPlanoSaude,
        BigDecimal descontoSindical,
        BigDecimal outrosDescontos,
        BigDecimal salarioLiquido,
        BigDecimal fgts,
        BigDecimal inssEmpresa
) {}
