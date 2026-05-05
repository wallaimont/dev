package com.seguradora.site.service;

import com.seguradora.site.model.LeadRequest;
import org.springframework.stereotype.Service;

@Service
public class FormInputNormalizationService {

    public LeadRequest normalize(LeadRequest request) {
        if (request == null) {
            return null;
        }

        request.setNome(normalizeText(request.getNome()));
        request.setEmail(normalizeEmail(request.getEmail()));
        request.setTelefone(normalizeText(request.getTelefone()));
        request.setTipoSeguro(normalizeText(request.getTipoSeguro()));
        request.setCobertura(normalizeText(request.getCobertura()));
        request.setMensagem(normalizeText(request.getMensagem()));
        request.setOrigem(normalizeText(request.getOrigem()));
        request.setSubmissionToken(normalizeText(request.getSubmissionToken()));
        return request;
    }

    private String normalizeEmail(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toLowerCase();
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String collapsed = value.replaceAll("\\s+", " ").trim();
        return collapsed.isEmpty() ? null : collapsed;
    }
}
