package com.seguradora.site.api;

import com.seguradora.site.model.QuoteResult;

public record QuoteApiResponse(
        String tipoSeguro,
        String cobertura,
        double valorMensal,
        double franquia,
        String prazoRetorno
) {
    public static QuoteApiResponse from(QuoteResult quoteResult) {
        return new QuoteApiResponse(
                quoteResult.getTipoSeguro(),
                quoteResult.getCobertura(),
                quoteResult.getValorMensal(),
                quoteResult.getFranquia(),
                quoteResult.getPrazoRetorno()
        );
    }
}