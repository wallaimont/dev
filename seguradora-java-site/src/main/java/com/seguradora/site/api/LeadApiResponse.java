package com.seguradora.site.api;

import com.seguradora.site.entity.LeadRecord;

import java.time.LocalDateTime;

public record LeadApiResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String tipoSeguro,
        String cobertura,
        Double valorBem,
        String mensagem,
        String origem,
        Double valorMensalEstimado,
        Double franquiaEstimada,
        String prazoRetorno,
        LocalDateTime createdAt
) {
    public static LeadApiResponse from(LeadRecord leadRecord) {
        return new LeadApiResponse(
                leadRecord.getId(),
                leadRecord.getNome(),
                leadRecord.getEmail(),
                leadRecord.getTelefone(),
                leadRecord.getTipoSeguro(),
                leadRecord.getCobertura(),
                leadRecord.getValorBem(),
                leadRecord.getMensagem(),
                leadRecord.getOrigem(),
                leadRecord.getValorMensalEstimado(),
                leadRecord.getFranquiaEstimada(),
                leadRecord.getPrazoRetorno(),
                leadRecord.getCreatedAt()
        );
    }
}