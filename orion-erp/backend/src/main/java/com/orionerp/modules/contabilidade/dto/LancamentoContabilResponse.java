package com.orionerp.modules.contabilidade.dto;

import com.orionerp.modules.contabilidade.domain.LancamentoContabil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LancamentoContabilResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        String lote,
        String sublote,
        Integer numero,
        LocalDate dataLancamento,
        Long contaDebitoId,
        Long contaCreditoId,
        BigDecimal valor,
        String historico,
        String documento,
        Long centroCustoId,
        Long centroResultadoId,
        String tipo,
        String origem,
        Long origemId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LancamentoContabilResponse from(LancamentoContabil e) {
        return new LancamentoContabilResponse(
                e.getId(), e.getUuid(), e.getEmpresaId(), e.getFilialId(),
                e.getLote(), e.getSublote(), e.getNumero(), e.getDataLancamento(),
                e.getContaDebitoId(), e.getContaCreditoId(), e.getValor(),
                e.getHistorico(), e.getDocumento(), e.getCentroCustoId(),
                e.getCentroResultadoId(), e.getTipo(), e.getOrigem(), e.getOrigemId(),
                e.getStatus(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
