package com.orionerp.modules.financeiro.dto;

import com.orionerp.modules.financeiro.domain.ConciliacaoBancaria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConciliacaoBancariaResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long contaBancariaId,
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal saldoExtrato,
        BigDecimal saldoSistema,
        BigDecimal diferenca,
        String status,
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ConciliacaoBancariaResponse from(ConciliacaoBancaria e) {
        return new ConciliacaoBancariaResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getContaBancariaId(), e.getDataInicio(), e.getDataFim(),
                e.getSaldoExtrato(), e.getSaldoSistema(), e.getDiferenca(),
                e.getStatus(), e.getObservacao(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
