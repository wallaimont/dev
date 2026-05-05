package com.orionerp.modules.patrimonio.dto;

import com.orionerp.modules.patrimonio.domain.BemPatrimonial;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BemPatrimonialResponse(
        Long id,
        Long empresaId,
        Long filialId,
        String codigo,
        String descricao,
        String numeroPatrimonio,
        LocalDate dataAquisicao,
        BigDecimal valorAquisicao,
        BigDecimal valorResidual,
        Integer vidaUtilMeses,
        BigDecimal taxaDepreciacao,
        String grupo,
        String localizacao,
        Long centroCustoId,
        Long fornecedorId,
        Long notaFiscalId,
        String status,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BemPatrimonialResponse from(BemPatrimonial e) {
        return new BemPatrimonialResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getCodigo(), e.getDescricao(), e.getNumeroPatrimonio(),
                e.getDataAquisicao(), e.getValorAquisicao(), e.getValorResidual(),
                e.getVidaUtilMeses(), e.getTaxaDepreciacao(),
                e.getGrupo(), e.getLocalizacao(),
                e.getCentroCustoId(), e.getFornecedorId(), e.getNotaFiscalId(),
                e.getStatus(), e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
