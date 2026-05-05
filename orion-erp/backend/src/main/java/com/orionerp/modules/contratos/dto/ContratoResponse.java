package com.orionerp.modules.contratos.dto;

import com.orionerp.modules.contratos.domain.Contrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratoResponse(
        Long id,
        Long empresaId,
        Long filialId,
        String numero,
        Long clienteId,
        String tipo,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim,
        LocalDate dataAssinatura,
        BigDecimal valorTotal,
        BigDecimal valorMensal,
        String formaPagamento,
        Integer diaVencimento,
        String status,
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ContratoResponse from(Contrato e) {
        return new ContratoResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getNumero(), e.getClienteId(), e.getTipo(),
                e.getDescricao(), e.getDataInicio(), e.getDataFim(),
                e.getDataAssinatura(), e.getValorTotal(), e.getValorMensal(),
                e.getFormaPagamento(), e.getDiaVencimento(),
                e.getStatus(), e.getObservacao(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
