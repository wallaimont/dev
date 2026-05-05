package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.PontoEletronico;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record PontoEletronicoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long funcionarioId,
        LocalDate data,
        LocalTime entrada1,
        LocalTime saida1,
        LocalTime entrada2,
        LocalTime saida2,
        LocalTime entrada3,
        LocalTime saida3,
        BigDecimal horasTrabalhadas,
        BigDecimal horasExtras,
        BigDecimal horasFalta,
        String tipo,
        Boolean aprovado,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PontoEletronicoResponse from(PontoEletronico p) {
        return new PontoEletronicoResponse(
                p.getId(), p.getUuid(), p.getEmpresaId(), p.getFilialId(),
                p.getFuncionarioId(), p.getData(), p.getEntrada1(), p.getSaida1(),
                p.getEntrada2(), p.getSaida2(), p.getEntrada3(), p.getSaida3(),
                p.getHorasTrabalhadas(), p.getHorasExtras(), p.getHorasFalta(),
                p.getTipo(), p.getAprovado(), p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
