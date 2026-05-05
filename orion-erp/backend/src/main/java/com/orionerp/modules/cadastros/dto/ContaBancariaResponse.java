package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.ContaBancaria;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContaBancariaResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long bancoId,
        String agencia,
        String conta,
        String digito,
        String tipo,
        String descricao,
        BigDecimal saldoInicial,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ContaBancariaResponse from(ContaBancaria e) {
        return new ContaBancariaResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getFilialId(),
                e.getBanco() != null ? e.getBanco().getId() : null,
                e.getAgencia(),
                e.getConta(),
                e.getDigito(),
                e.getTipo(),
                e.getDescricao(),
                e.getSaldoInicial(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
