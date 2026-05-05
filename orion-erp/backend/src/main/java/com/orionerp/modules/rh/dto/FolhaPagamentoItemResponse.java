package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.FolhaPagamentoItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FolhaPagamentoItemResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long folhaPagamentoId,
        Long funcionarioId,
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
        BigDecimal inssEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FolhaPagamentoItemResponse from(FolhaPagamentoItem i) {
        return new FolhaPagamentoItemResponse(
                i.getId(), i.getUuid(), i.getEmpresaId(), i.getFilialId(),
                i.getFolhaPagamentoId(), i.getFuncionarioId(), i.getSalarioBase(),
                i.getHorasExtrasValor(), i.getAdicionalNoturno(), i.getAdicionalPericulosidade(),
                i.getAdicionalInsalubridade(), i.getComissoes(), i.getGratificacoes(),
                i.getOutrosProventos(), i.getDescontoInss(), i.getDescontoIrrf(),
                i.getDescontoVt(), i.getDescontoVr(), i.getDescontoPlanoSaude(),
                i.getDescontoSindical(), i.getOutrosDescontos(), i.getSalarioLiquido(),
                i.getFgts(), i.getInssEmpresa(), i.getCreatedAt(), i.getUpdatedAt()
        );
    }
}
