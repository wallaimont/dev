package com.orionerp.modules.contratos.dto;

import com.orionerp.modules.contratos.domain.ContratoParcela;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratoParcelaResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long contratoId,
        Integer numeroParcela,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valor,
        BigDecimal valorPago,
        String status,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ContratoParcelaResponse from(ContratoParcela e) {
        return new ContratoParcelaResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getContratoId(), e.getNumeroParcela(),
                e.getDataVencimento(), e.getDataPagamento(),
                e.getValor(), e.getValorPago(), e.getStatus(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
