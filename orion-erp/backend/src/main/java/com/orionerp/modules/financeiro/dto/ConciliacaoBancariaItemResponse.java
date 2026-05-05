package com.orionerp.modules.financeiro.dto;

import com.orionerp.modules.financeiro.domain.ConciliacaoBancariaItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConciliacaoBancariaItemResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long conciliacaoId,
        Long lancamentoFinanceiroId,
        LocalDate dataExtrato,
        String descricaoExtrato,
        BigDecimal valorExtrato,
        Boolean conciliado,
        LocalDate dataConciliacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ConciliacaoBancariaItemResponse from(ConciliacaoBancariaItem e) {
        return new ConciliacaoBancariaItemResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getConciliacaoId(), e.getLancamentoFinanceiroId(),
                e.getDataExtrato(), e.getDescricaoExtrato(),
                e.getValorExtrato(), e.getConciliado(), e.getDataConciliacao(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
