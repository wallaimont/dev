package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.Beneficio;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BeneficioResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String nome,
        String tipo,
        BigDecimal valorEmpresa,
        BigDecimal valorFuncionario,
        Boolean descontoFolha,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BeneficioResponse from(Beneficio b) {
        return new BeneficioResponse(
                b.getId(), b.getUuid(), b.getEmpresaId(), b.getCodigo(), b.getNome(),
                b.getTipo(), b.getValorEmpresa(), b.getValorFuncionario(), b.getDescontoFolha(),
                b.getAtivo(), b.getCreatedAt(), b.getUpdatedAt()
        );
    }
}
