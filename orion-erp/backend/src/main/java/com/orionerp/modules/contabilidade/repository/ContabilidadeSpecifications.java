package com.orionerp.modules.contabilidade.repository;

import com.orionerp.modules.contabilidade.domain.LancamentoContabil;
import com.orionerp.modules.contabilidade.domain.PlanoContas;
import com.orionerp.modules.contabilidade.domain.CentroResultado;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class ContabilidadeSpecifications {

    private ContabilidadeSpecifications() {}

    public static Specification<PlanoContas> planoContasFilter(Long empresaId, String search, String tipo) {
        Specification<PlanoContas> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (search != null && !search.isBlank()) {
            String term = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("codigo")), term),
                    cb.like(cb.lower(root.get("descricao")), term),
                    cb.like(cb.lower(root.get("classificacao")), term)
            ));
        }
        if (tipo != null && !tipo.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        return spec;
    }

    public static Specification<CentroResultado> centroResultadoFilter(Long empresaId, String search) {
        Specification<CentroResultado> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (search != null && !search.isBlank()) {
            String term = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("codigo")), term),
                    cb.like(cb.lower(root.get("descricao")), term)
            ));
        }
        return spec;
    }

    public static Specification<LancamentoContabil> lancamentoFilter(Long empresaId, String lote,
                                                                      LocalDate dataInicio, LocalDate dataFim, String status) {
        Specification<LancamentoContabil> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (lote != null && !lote.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("lote"), lote));
        }
        if (dataInicio != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataLancamento"), dataInicio));
        }
        if (dataFim != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dataLancamento"), dataFim));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        return spec;
    }
}
