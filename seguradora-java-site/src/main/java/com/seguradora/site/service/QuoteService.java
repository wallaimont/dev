package com.seguradora.site.service;

import com.seguradora.site.model.LeadRequest;
import com.seguradora.site.model.QuoteResult;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class QuoteService {

    private static final Map<String, Double> INSURANCE_RATE = Map.of(
            "Seguro Auto", 0.028,
            "Seguro Residencial", 0.012,
            "Seguro Empresarial", 0.019,
            "Seguro de Vida", 0.009
    );

    private static final Map<String, Double> COVERAGE_FACTOR = Map.of(
            "Essencial", 1.0,
            "Completa", 1.25,
            "Premium", 1.55
    );

    public QuoteResult generate(LeadRequest leadRequest) {
        String tipoSeguro = safeValue(leadRequest.getTipoSeguro(), "Seguro Auto");
        String cobertura = safeValue(leadRequest.getCobertura(), "Completa");
        double valorBem = leadRequest.getValorBem() != null && leadRequest.getValorBem() > 0
                ? leadRequest.getValorBem()
                : 50000.0;

        double baseRate = INSURANCE_RATE.getOrDefault(tipoSeguro, 0.015);
        double coverageFactor = COVERAGE_FACTOR.getOrDefault(cobertura, 1.15);
        double valorMensal = (valorBem * baseRate * coverageFactor) / 12;
        double franquia = valorBem * (0.03 + (coverageFactor / 100));
        String prazoRetorno = valorBem >= 150000 ? "até 30 minutos" : "em até 2 horas";

        return new QuoteResult(
                tipoSeguro,
                cobertura,
                round(valorMensal),
                round(franquia),
                prazoRetorno
        );
    }

    private String safeValue(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
