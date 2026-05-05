package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.CondicaoPagamentoParcela;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CondicaoPagamentoParcelaResponse {

    Long id;
    Long condicaoPagamentoId;
    Integer numeroParcela;
    Integer dias;
    BigDecimal percentual;

    public static CondicaoPagamentoParcelaResponse from(CondicaoPagamentoParcela entity) {
        return CondicaoPagamentoParcelaResponse.builder()
                .id(entity.getId())
                .condicaoPagamentoId(entity.getCondicaoPagamento().getId())
                .numeroParcela(entity.getNumeroParcela())
                .dias(entity.getDias())
                .percentual(entity.getPercentual())
                .build();
    }
}
