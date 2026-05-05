package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.FolhaPagamento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FolhaPagamentoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Integer ano,
        Integer mes,
        String tipo,
        LocalDate dataCalculo,
        LocalDate dataPagamento,
        BigDecimal totalProventos,
        BigDecimal totalDescontos,
        BigDecimal totalLiquido,
        BigDecimal totalEncargos,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FolhaPagamentoResponse from(FolhaPagamento fp) {
        return new FolhaPagamentoResponse(
                fp.getId(), fp.getUuid(), fp.getEmpresaId(), fp.getFilialId(),
                fp.getAno(), fp.getMes(), fp.getTipo(), fp.getDataCalculo(), fp.getDataPagamento(),
                fp.getTotalProventos(), fp.getTotalDescontos(), fp.getTotalLiquido(), fp.getTotalEncargos(),
                fp.getStatus(), fp.getCreatedAt(), fp.getUpdatedAt()
        );
    }
}
