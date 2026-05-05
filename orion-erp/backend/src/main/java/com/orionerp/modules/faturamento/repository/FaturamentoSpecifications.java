package com.orionerp.modules.faturamento.repository;

import com.orionerp.modules.faturamento.domain.NotaFiscal;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FaturamentoSpecifications {

    private FaturamentoSpecifications() {}

    public static Specification<NotaFiscal> notaFiscalFilter(Long empresaId, String tipo, String numero,
                                                              LocalDate dataInicio, LocalDate dataFim, String status) {
        Specification<NotaFiscal> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);

        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (tipo != null && !tipo.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        if (numero != null && !numero.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("numero")),
                    "%" + numero.toLowerCase() + "%"));
        }
        if (dataInicio != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataEmissao"), dataInicio));
        }
        if (dataFim != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dataEmissao"), dataFim));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        return spec;
    }
}
